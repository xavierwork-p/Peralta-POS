package com.peraltapos.sales.sale;

import com.peraltapos.accounting.AccountingPostingService;
import com.peraltapos.accounting.receivable.AccountReceivable;
import com.peraltapos.accounting.receivable.AccountReceivableRepository;
import com.peraltapos.catalog.inventory.InventoryMovementService;
import com.peraltapos.catalog.product.Product;
import com.peraltapos.catalog.product.ProductRepository;
import com.peraltapos.common.web.BusinessException;
import com.peraltapos.crm.customer.Customer;
import com.peraltapos.crm.customer.CustomerFiscalProfile;
import com.peraltapos.crm.customer.CustomerRepository;
import com.peraltapos.fiscal.NcfService;
import com.peraltapos.sales.quote.Quote;
import com.peraltapos.sales.quote.QuoteItem;
import com.peraltapos.sales.quote.QuoteStatus;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class SaleService {

    private static final DateTimeFormatter INVOICE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final AccountReceivableRepository accountReceivableRepository;
    private final InventoryMovementService inventoryMovementService;
    private final NcfService ncfService;
    private final AccountingPostingService accountingPostingService;

    public SaleService(
            SaleRepository saleRepository,
            ProductRepository productRepository,
            CustomerRepository customerRepository,
            AccountReceivableRepository accountReceivableRepository,
            InventoryMovementService inventoryMovementService,
            NcfService ncfService,
            AccountingPostingService accountingPostingService
    ) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.accountReceivableRepository = accountReceivableRepository;
        this.inventoryMovementService = inventoryMovementService;
        this.ncfService = ncfService;
        this.accountingPostingService = accountingPostingService;
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> list(String search) {
        Sort sort = Sort.by("issuedAt").descending().and(Sort.by("invoiceNumber").descending());
        List<Sale> sales = (search == null || search.isBlank())
                ? saleRepository.findAll(sort)
                : saleRepository.findByCustomerNameContainingIgnoreCaseOrInvoiceNumberContainingIgnoreCase(search, search, sort);

        return sales.stream().map(SaleResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public SaleResponse get(UUID id) {
        return SaleResponse.from(findEntity(id));
    }

    @Transactional
    public SaleResponse create(SaleRequest request) {
        Customer customer = resolveCustomer(request);
        NcfService.IssuedNcf issuedNcf = ncfService.issueForSale(customer, request.customerFiscalId());
        Sale sale = new Sale();
        sale.issue(generateInvoiceNumber(), issuedNcf.ncf(), issuedNcf.documentType(), customer, request);

        List<SaleItem> items = request.items().stream()
                .map(itemRequest -> createItemAndDiscountStock(itemRequest, customer, sale.getInvoiceNumber()))
                .toList();
        sale.replaceItems(items);
        sale.replacePayments(request.payments().stream().map(Payment::from).toList());

        validatePayments(sale, customer);

        Sale savedSale = saleRepository.save(sale);
        createAccountReceivableForCredit(savedSale, customer);
        accountingPostingService.postSale(savedSale);

        return SaleResponse.from(savedSale);
    }

    @Transactional
    public Sale createFromApprovedQuote(Quote quote) {
        if (quote.getStatus() != QuoteStatus.APPROVED) {
            throw new BusinessException("Solo se puede facturar una cotizacion aprobada");
        }

        Customer customer = resolveCustomerForQuote(quote);
        NcfService.IssuedNcf issuedNcf = ncfService.issueForSale(customer, quote.getCustomerFiscalId());

        Sale sale = new Sale();
        sale.issueFromQuote(generateInvoiceNumber(), issuedNcf.ncf(), issuedNcf.documentType(), customer, quote);

        List<SaleItem> items = quote.getItems().stream()
                .map(item -> createItemAndDiscountStock(item, sale.getInvoiceNumber()))
                .toList();
        sale.replaceItems(items);
        sale.replacePayments(List.of(Payment.from(new PaymentRequest(
                PaymentMethod.CREDIT,
                quote.getTotal(),
                null,
                "Cotizacion " + quote.getQuoteNumber()
        ))));

        validatePayments(sale, customer);

        Sale savedSale = saleRepository.save(sale);
        createAccountReceivableForCredit(savedSale, customer);
        accountingPostingService.postSale(savedSale);
        return savedSale;
    }

    @Transactional(readOnly = true)
    public BigDecimal salesToday() {
        OffsetDateTime start = LocalDate.now().atStartOfDay().atOffset(OffsetDateTime.now().getOffset());
        OffsetDateTime end = start.plusDays(1);
        return saleRepository.sumSalesBetweenByStatus(start, end, SaleStatus.ISSUED);
    }

    private SaleItem createItemAndDiscountStock(SaleItemRequest request, Customer customer, String invoiceNumber) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new BusinessException("Producto no encontrado"));

        if (!product.isActive()) {
            throw new BusinessException("El producto " + product.getName() + " esta inactivo");
        }

        if (!product.hasStockFor(request.quantity())) {
            throw new BusinessException("Stock insuficiente para " + product.getName());
        }

        product.decreaseStock(request.quantity());
        inventoryMovementService.recordSale(product, request.quantity(), invoiceNumber);
        return SaleItem.from(product, request.quantity(), taxRateFor(product, customer));
    }

    private SaleItem createItemAndDiscountStock(QuoteItem request, String invoiceNumber) {
        Product product = request.getProduct();
        if (product == null) {
            product = productRepository.findFirstByNameIgnoreCase(request.getProductName())
                    .orElseThrow(() -> new BusinessException("La linea " + request.getProductName() + " no esta vinculada a un producto"));
        }

        if (!product.isActive()) {
            throw new BusinessException("El producto " + product.getName() + " esta inactivo");
        }

        if (!product.hasStockFor(request.getQuantity())) {
            throw new BusinessException("Stock insuficiente para " + product.getName());
        }

        product.decreaseStock(request.getQuantity());
        inventoryMovementService.recordSale(product, request.getQuantity(), invoiceNumber);
        return SaleItem.fromQuotedLine(
                product,
                request.getProductName(),
                request.getQuantity(),
                request.getUnitPrice(),
                request.getTaxRate()
        );
    }

    private BigDecimal taxRateFor(Product product, Customer customer) {
        if (customer != null && isTaxExemptProfile(customer.getFiscalProfile())) {
            return BigDecimal.ZERO;
        }

        return product.getTaxRate();
    }

    private boolean isTaxExemptProfile(CustomerFiscalProfile fiscalProfile) {
        return fiscalProfile == CustomerFiscalProfile.FREE_ZONE
                || fiscalProfile == CustomerFiscalProfile.GOVERNMENT
                || fiscalProfile == CustomerFiscalProfile.SPECIAL_REGIME;
    }

    private Customer resolveCustomer(SaleRequest request) {
        if (request.customerId() == null) {
            return null;
        }
        return customerRepository.findById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cliente no encontrado"));
    }

    private Customer resolveCustomerForQuote(Quote quote) {
        if (quote.getCustomerFiscalId() != null && !quote.getCustomerFiscalId().isBlank()) {
            return customerRepository.findByFiscalId(quote.getCustomerFiscalId().trim())
                    .orElseThrow(() -> new BusinessException("Registra el cliente con ese RNC antes de facturar la cotizacion"));
        }

        return customerRepository.findFirstByNameIgnoreCase(quote.getCustomerName())
                .orElseThrow(() -> new BusinessException("Registra el cliente antes de facturar la cotizacion"));
    }

    private void validatePayments(Sale sale, Customer customer) {
        validateCardPayments(sale);
        validateCreditPayments(sale, customer);

        BigDecimal paid = sale.getPayments()
                .stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (paid.compareTo(sale.getTotal()) < 0) {
            throw new BusinessException("El monto pagado no cubre el total de la venta");
        }
    }

    private void validateCardPayments(Sale sale) {
        List<Payment> cardPayments = sale.getPayments()
                .stream()
                .filter(payment -> payment.getMethod() == PaymentMethod.CARD)
                .toList();

        for (Payment payment : cardPayments) {
            if (payment.getProcessor() == null) {
                throw new BusinessException("El pago con tarjeta necesita seleccionar Azul o CardNet");
            }

            if (payment.getReference() == null || payment.getReference().isBlank()) {
                throw new BusinessException("El pago con tarjeta necesita numero de comprobante");
            }
        }
    }

    private void validateCreditPayments(Sale sale, Customer customer) {
        BigDecimal creditAmount = creditAmountFor(sale);

        if (creditAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        if (customer == null) {
            throw new BusinessException("El credito solo esta disponible para clientes registrados");
        }

        if (!customer.isActive()) {
            throw new BusinessException("El cliente seleccionado esta inactivo para credito");
        }

        if (customer.getCreditLimit().compareTo(creditAmount) < 0) {
            throw new BusinessException("El monto excede el limite de credito del cliente");
        }
    }

    private void createAccountReceivableForCredit(Sale sale, Customer customer) {
        BigDecimal creditAmount = creditAmountFor(sale);

        if (creditAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        LocalDate dueDate = LocalDate.now().plusDays(30);
        accountReceivableRepository.save(AccountReceivable.fromCreditSale(customer, sale, creditAmount, dueDate));
    }

    private BigDecimal creditAmountFor(Sale sale) {
        return sale.getPayments()
                .stream()
                .filter(payment -> payment.getMethod() == PaymentMethod.CREDIT)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String generateInvoiceNumber() {
        // Generador simple para la fase inicial. Luego se reemplaza por secuencias
        // por sucursal/caja y por la logica fiscal NCF correspondiente.
        long next = saleRepository.count() + 1;
        return "FAC-" + LocalDate.now().format(INVOICE_DATE_FORMAT) + "-" + String.format("%05d", next);
    }

    private Sale findEntity(UUID id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Venta no encontrada"));
    }
}
