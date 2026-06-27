package com.peraltapos.purchasing.order;

import com.peraltapos.catalog.supplier.Supplier;
import com.peraltapos.common.domain.BaseEntity;
import com.peraltapos.common.web.BusinessException;
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
@Table(name = "purchase_orders")
public class PurchaseOrder extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "order_number", nullable = false, unique = true, length = 40)
    private String orderNumber;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "expected_date")
    private LocalDate expectedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PurchaseOrderStatus status = PurchaseOrderStatus.OPEN;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "tax_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal taxTotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(length = 500)
    private String notes;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderItem> items = new ArrayList<>();

    public void open(String orderNumber, Supplier supplier, PurchaseOrderRequest request, List<PurchaseOrderItem> newItems) {
        this.orderNumber = orderNumber;
        this.supplier = supplier;
        orderDate = LocalDate.now();
        expectedDate = request.expectedDate();
        notes = cleanText(request.notes());
        replaceItems(newItems);
    }

    public void markReceived() {
        if (status != PurchaseOrderStatus.OPEN) {
            throw new BusinessException("La orden no esta disponible para recibir");
        }
        status = PurchaseOrderStatus.RECEIVED;
    }

    public void cancel() {
        if (status != PurchaseOrderStatus.OPEN) {
            throw new BusinessException("Solo se pueden cancelar ordenes abiertas");
        }
        status = PurchaseOrderStatus.CANCELLED;
    }

    private void replaceItems(List<PurchaseOrderItem> newItems) {
        items.clear();
        for (PurchaseOrderItem item : newItems) {
            item.attachTo(this);
            items.add(item);
        }
        subtotal = items.stream().map(PurchaseOrderItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        taxTotal = items.stream().map(PurchaseOrderItem::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        total = subtotal.add(taxTotal);
    }

    private String cleanText(String value) {
        String cleanValue = value == null ? null : value.trim();
        return cleanValue == null || cleanValue.isBlank() ? null : cleanValue;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public LocalDate getExpectedDate() {
        return expectedDate;
    }

    public PurchaseOrderStatus getStatus() {
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

    public List<PurchaseOrderItem> getItems() {
        return items;
    }
}
