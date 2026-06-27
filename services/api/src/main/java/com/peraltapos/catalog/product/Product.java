package com.peraltapos.catalog.product;

import com.peraltapos.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product extends BaseEntity {

    @Column(nullable = false, unique = true, length = 40)
    private String sku;

    @Column(length = 80)
    private String barcode;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(length = 600)
    private String description;

    @Column(name = "category_name", length = 120)
    private String categoryName;

    @Column(name = "brand_name", length = 120)
    private String brandName;

    @Column(nullable = false, length = 40)
    private String unit;

    @Column(name = "cost_price", nullable = false, precision = 14, scale = 2)
    private BigDecimal costPrice = BigDecimal.ZERO;

    @Column(name = "sale_price", nullable = false, precision = 14, scale = 2)
    private BigDecimal salePrice = BigDecimal.ZERO;

    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate = new BigDecimal("18.00");

    @Column(name = "current_stock", nullable = false, precision = 14, scale = 2)
    private BigDecimal currentStock = BigDecimal.ZERO;

    @Column(name = "minimum_stock", nullable = false, precision = 14, scale = 2)
    private BigDecimal minimumStock = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean active = true;

    public void updateFrom(ProductRequest request) {
        sku = request.sku();
        barcode = request.barcode();
        name = request.name();
        description = request.description();
        categoryName = request.categoryName();
        brandName = request.brandName();
        unit = request.unit();
        costPrice = request.costPrice();
        salePrice = request.salePrice();
        taxRate = request.taxRate();
        currentStock = request.currentStock();
        minimumStock = request.minimumStock();
        active = request.active();
    }

    public void updateCatalogFrom(ProductRequest request) {
        sku = request.sku();
        barcode = request.barcode();
        name = request.name();
        description = request.description();
        categoryName = request.categoryName();
        brandName = request.brandName();
        unit = request.unit();
        costPrice = request.costPrice();
        salePrice = request.salePrice();
        taxRate = request.taxRate();
        minimumStock = request.minimumStock();
        active = request.active();
    }

    public boolean hasStockFor(BigDecimal quantity) {
        return currentStock.compareTo(quantity) >= 0;
    }

    public void decreaseStock(BigDecimal quantity) {
        currentStock = currentStock.subtract(quantity);
    }

    public void increaseStock(BigDecimal quantity) {
        currentStock = currentStock.add(quantity);
    }

    public void updateCostPrice(BigDecimal newCostPrice) {
        costPrice = newCostPrice;
    }

    public String getSku() {
        return sku;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getUnit() {
        return unit;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public BigDecimal getCurrentStock() {
        return currentStock;
    }

    public BigDecimal getMinimumStock() {
        return minimumStock;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isLowStock() {
        return active && currentStock.compareTo(minimumStock) <= 0;
    }
}
