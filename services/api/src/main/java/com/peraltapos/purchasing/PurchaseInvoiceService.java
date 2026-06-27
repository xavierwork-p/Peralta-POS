package com.peraltapos.purchasing;

import com.peraltapos.accounting.payable.AccountPayable;
import com.peraltapos.accounting.payable.AccountPayableRepository;
import com.peraltapos.accounting.AccountingPostingService;
import com.peraltapos.catalog.inventory.InventoryMovementService;
import com.peraltapos.catalog.product.Product;
import com.peraltapos.catalog.product.ProductRepository;
import com.peraltapos.catalog.supplier.Supplier;
import com.peraltapos.catalog.supplier.SupplierRepository;
import com.peraltapos.common.web.BusinessException;
import com.peraltapos.purchasing.order.PurchaseOrder;
import com.peraltapos.purchasing.order.PurchaseOrderService;
import com.peraltapos.purchasing.order.PurchaseOrderStatus;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PurchaseInvoiceService {

    private final PurchaseInvoiceRepository purchaseInvoiceRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final InventoryMovementService inventoryMovementService;
    private final AccountPayableRepository accountPayableRepository;
    private final PurchaseOrderService purchaseOrderService;
    private final AccountingPostingService accountingPostingService;

    public PurchaseInvoiceService(
            PurchaseInvoiceRepository purchaseInvoiceRepository,
            SupplierRepository supplierRepository,
            ProductRepository productRepository,
            InventoryMovementService inventoryMovementService,
            AccountPayableRepository accountPayableRepository,
            PurchaseOrderService purchaseOrderService,
            AccountingPostingService accountingPostingService
    ) {
        this.purchaseInvoiceRepository = purchaseInvoiceRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.inventoryMovementService = inventoryMovementService;
        this.accountPayableRepository = accountPayableRepository;
        this.purchaseOrderService = purchaseOrderService;
        this.accountingPostingService = accountingPostingService;
    }

    @Transactional(readOnly = true)
    public List<PurchaseInvoiceResponse> list(String search) {
        Sort sort = Sort.by("invoiceDate").descending().and(Sort.by("createdAt").descending());
        List<PurchaseInvoice> invoices = (search == null || search.isBlank())
                ? purchaseInvoiceRepository.findAll(sort)
                : purchaseInvoiceRepository.findBySupplier_NameContainingIgnoreCaseOrDocumentNumberContainingIgnoreCase(
                        search,
                        search,
                        sort
                );

        return invoices.stream().map(PurchaseInvoiceResponse::from).toList();
    }

    @Transactional
    public PurchaseInvoiceResponse create(PurchaseInvoiceRequest request) {
        PurchaseOrder purchaseOrder = resolvePurchaseOrder(request);
        Supplier supplier = supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new BusinessException("Suplidor no encontrado"));

        if (!supplier.isActive()) {
            throw new BusinessException("El suplidor seleccionado esta inactivo");
        }

        purchaseInvoiceRepository
                .findBySupplier_IdAndDocumentNumberIgnoreCase(supplier.getId(), request.documentNumber().trim())
                .ifPresent(existing -> {
                    throw new BusinessException("Esa factura ya fue registrada para el suplidor");
                });

        List<PurchaseInvoiceItem> items = new ArrayList<>();
        for (PurchaseInvoiceItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new BusinessException("Producto no encontrado"));

            if (!product.isActive()) {
                throw new BusinessException("El producto " + product.getName() + " esta inactivo");
            }

            product.increaseStock(itemRequest.quantity());
            product.updateCostPrice(itemRequest.unitCost());
            items.add(PurchaseInvoiceItem.from(product, itemRequest));
        }

        PurchaseInvoice invoice = new PurchaseInvoice();
        invoice.post(purchaseOrder, supplier, request, items);
        PurchaseInvoice savedInvoice = purchaseInvoiceRepository.save(invoice);

        for (PurchaseInvoiceItem item : savedInvoice.getItems()) {
            inventoryMovementService.recordPurchase(
                    item.getProduct(),
                    item.getQuantity(),
                    item.getUnitCost(),
                    savedInvoice.getDocumentNumber()
            );
        }

        if (savedInvoice.getPaymentTerm() == PurchasePaymentTerm.CREDIT) {
            accountPayableRepository.save(AccountPayable.fromPurchaseInvoice(savedInvoice));
        }
        accountingPostingService.postPurchaseInvoice(savedInvoice);

        if (purchaseOrder != null) {
            purchaseOrder.markReceived();
        }

        return PurchaseInvoiceResponse.from(savedInvoice);
    }

    private PurchaseOrder resolvePurchaseOrder(PurchaseInvoiceRequest request) {
        if (request.purchaseOrderId() == null) {
            return null;
        }

        PurchaseOrder order = purchaseOrderService.findEntity(request.purchaseOrderId());
        if (order.getStatus() != PurchaseOrderStatus.OPEN) {
            throw new BusinessException("La orden de compra ya fue recibida o cancelada");
        }
        if (!order.getSupplier().getId().equals(request.supplierId())) {
            throw new BusinessException("El suplidor de la factura no coincide con la orden");
        }
        return order;
    }
}
