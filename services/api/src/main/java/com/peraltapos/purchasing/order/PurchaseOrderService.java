package com.peraltapos.purchasing.order;

import com.peraltapos.catalog.product.Product;
import com.peraltapos.catalog.product.ProductRepository;
import com.peraltapos.catalog.supplier.Supplier;
import com.peraltapos.catalog.supplier.SupplierRepository;
import com.peraltapos.common.web.BusinessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PurchaseOrderService {

    private static final DateTimeFormatter ORDER_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    public PurchaseOrderService(
            PurchaseOrderRepository purchaseOrderRepository,
            SupplierRepository supplierRepository,
            ProductRepository productRepository
    ) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrderResponse> list(String search) {
        Sort sort = Sort.by("orderDate").descending().and(Sort.by("createdAt").descending());
        List<PurchaseOrder> orders = (search == null || search.isBlank())
                ? purchaseOrderRepository.findAll(sort)
                : purchaseOrderRepository.findBySupplier_NameContainingIgnoreCaseOrOrderNumberContainingIgnoreCase(
                        search,
                        search,
                        sort
                );

        return orders.stream().map(PurchaseOrderResponse::from).toList();
    }

    @Transactional
    public PurchaseOrderResponse create(PurchaseOrderRequest request) {
        Supplier supplier = supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new BusinessException("Suplidor no encontrado"));

        if (!supplier.isActive()) {
            throw new BusinessException("El suplidor seleccionado esta inactivo");
        }

        Set<UUID> productIds = new HashSet<>();
        List<PurchaseOrderItem> items = new ArrayList<>();
        for (PurchaseOrderItemRequest itemRequest : request.items()) {
            if (!productIds.add(itemRequest.productId())) {
                throw new BusinessException("Un producto no puede repetirse en la misma orden");
            }

            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new BusinessException("Producto no encontrado"));
            if (!product.isActive()) {
                throw new BusinessException("El producto " + product.getName() + " esta inactivo");
            }
            items.add(PurchaseOrderItem.from(product, itemRequest));
        }

        PurchaseOrder order = new PurchaseOrder();
        order.open(generateOrderNumber(), supplier, request, items);
        return PurchaseOrderResponse.from(purchaseOrderRepository.save(order));
    }

    @Transactional
    public PurchaseOrderResponse cancel(UUID id) {
        PurchaseOrder order = findEntity(id);
        order.cancel();
        return PurchaseOrderResponse.from(purchaseOrderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public PurchaseOrder findEntity(UUID id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Orden de compra no encontrada"));
    }

    private String generateOrderNumber() {
        long next = purchaseOrderRepository.count() + 1;
        return "OC-" + LocalDate.now().format(ORDER_DATE_FORMAT) + "-" + String.format("%05d", next);
    }
}
