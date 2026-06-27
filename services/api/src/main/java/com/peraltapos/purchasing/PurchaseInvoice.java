package com.peraltapos.purchasing;

import com.peraltapos.catalog.supplier.Supplier;
import com.peraltapos.common.domain.BaseEntity;
import com.peraltapos.purchasing.order.PurchaseOrder;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_invoices")
public class PurchaseInvoice extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "document_number", nullable = false, length = 80)
    private String documentNumber;

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_term", nullable = false, length = 30)
    private PurchasePaymentTerm paymentTerm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PurchaseInvoiceStatus status = PurchaseInvoiceStatus.POSTED;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "tax_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal taxTotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(length = 500)
    private String notes;

    @OneToMany(mappedBy = "purchaseInvoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseInvoiceItem> items = new ArrayList<>();

    public void post(
            PurchaseOrder purchaseOrder,
            Supplier supplier,
            PurchaseInvoiceRequest request,
            List<PurchaseInvoiceItem> newItems
    ) {
        this.purchaseOrder = purchaseOrder;
        this.supplier = supplier;
        documentNumber = request.documentNumber().trim();
        invoiceDate = request.invoiceDate();
        dueDate = request.dueDate();
        paymentTerm = request.paymentTerm();
        notes = cleanText(request.notes());
        replaceItems(newItems);
    }

    private void replaceItems(List<PurchaseInvoiceItem> newItems) {
        items.clear();
        for (PurchaseInvoiceItem item : newItems) {
            item.attachTo(this);
            items.add(item);
        }
        subtotal = items.stream()
                .map(PurchaseInvoiceItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        taxTotal = items.stream()
                .map(PurchaseInvoiceItem::getTaxAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        total = subtotal.add(taxTotal);
    }

    private String cleanText(String value) {
        String cleanValue = value == null ? null : value.trim();
        return cleanValue == null || cleanValue.isBlank() ? null : cleanValue;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public PurchasePaymentTerm getPaymentTerm() {
        return paymentTerm;
    }

    public PurchaseInvoiceStatus getStatus() {
        return status;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getTaxTotal() {
        return taxTotal;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getNotes() {
        return notes;
    }

    public List<PurchaseInvoiceItem> getItems() {
        return items;
    }
}
