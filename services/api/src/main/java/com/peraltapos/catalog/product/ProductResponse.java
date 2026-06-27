package com.peraltapos.catalog.product;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String sku,
        String barcode,
        String name,
        String description,
        String categoryName,
        String brandName,
        String unit,
        BigDecimal costPrice,
        BigDecimal salePrice,
        BigDecimal taxRate,
        BigDecimal currentStock,
        BigDecimal minimumStock,
        boolean lowStock,
        boolean active
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getBarcode(),
                product.getName(),
                product.getDescription(),
                product.getCategoryName(),
                product.getBrandName(),
                product.getUnit(),
                product.getCostPrice(),
                product.getSalePrice(),
                product.getTaxRate(),
                product.getCurrentStock(),
                product.getMinimumStock(),
                product.isLowStock(),
                product.isActive()
        );
    }
}
