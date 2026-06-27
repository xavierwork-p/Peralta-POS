package com.peraltapos.sales.sale;

import com.peraltapos.catalog.product.Product;
import com.peraltapos.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "sale_items")
public class SaleItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "product_name", nullable = false, length = 160)
    private String productName;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false, precision = 14, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "tax_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "line_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal lineTotal;

    public static SaleItem from(Product product, BigDecimal quantity) {
        return from(product, quantity, product.getTaxRate());
    }

    public static SaleItem from(Product product, BigDecimal quantity, BigDecimal taxRate) {
        SaleItem item = new SaleItem();
        item.product = product;
        item.productName = product.getName();
        item.quantity = quantity;
        item.unitPrice = product.getSalePrice();
        item.taxRate = taxRate;
        item.recalculate();
        return item;
    }

    public static SaleItem fromQuotedLine(
            Product product,
            String productName,
            BigDecimal quantity,
            BigDecimal unitPrice,
            BigDecimal taxRate
    ) {
        SaleItem item = new SaleItem();
        item.product = product;
        item.productName = productName;
        item.quantity = quantity;
        item.unitPrice = unitPrice;
        item.taxRate = taxRate;
        item.recalculate();
        return item;
    }

    void attachTo(Sale sale) {
        this.sale = sale;
    }

    private void recalculate() {
        subtotal = unitPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
        taxAmount = subtotal.multiply(taxRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        lineTotal = subtotal.add(taxAmount);
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }
}
