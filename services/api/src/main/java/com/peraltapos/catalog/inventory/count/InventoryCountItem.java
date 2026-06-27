package com.peraltapos.catalog.inventory.count;

import com.peraltapos.catalog.product.Product;
import com.peraltapos.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "inventory_count_items")
public class InventoryCountItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_count_id", nullable = false)
    private InventoryCount inventoryCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_sku", nullable = false, length = 40)
    private String productSku;

    @Column(name = "product_name", nullable = false, length = 160)
    private String productName;

    @Column(name = "expected_stock", nullable = false, precision = 14, scale = 2)
    private BigDecimal expectedStock;

    @Column(name = "counted_stock", nullable = false, precision = 14, scale = 2)
    private BigDecimal countedStock;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal difference;

    public static InventoryCountItem from(Product product, BigDecimal countedStock) {
        InventoryCountItem item = new InventoryCountItem();
        item.product = product;
        item.productSku = product.getSku();
        item.productName = product.getName();
        item.expectedStock = product.getCurrentStock();
        item.countedStock = countedStock;
        item.difference = countedStock.subtract(item.expectedStock);
        return item;
    }

    void attachTo(InventoryCount inventoryCount) {
        this.inventoryCount = inventoryCount;
    }

    public Product getProduct() {
        return product;
    }

    public String getProductSku() {
        return productSku;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getExpectedStock() {
        return expectedStock;
    }

    public BigDecimal getCountedStock() {
        return countedStock;
    }

    public BigDecimal getDifference() {
        return difference;
    }
}
