package com.peraltapos.catalog.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "El codigo interno es obligatorio")
        String sku,
        String barcode,
        @NotBlank(message = "El nombre del producto es obligatorio")
        String name,
        String description,
        String categoryName,
        String brandName,
        @NotBlank(message = "La unidad de medida es obligatoria")
        String unit,
        @NotNull(message = "El costo es obligatorio")
        @DecimalMin(value = "0.00", message = "El costo no puede ser negativo")
        BigDecimal costPrice,
        @NotNull(message = "El precio de venta es obligatorio")
        @DecimalMin(value = "0.00", message = "El precio de venta no puede ser negativo")
        BigDecimal salePrice,
        @NotNull(message = "El ITBIS es obligatorio")
        @DecimalMin(value = "0.00", message = "El ITBIS no puede ser negativo")
        BigDecimal taxRate,
        @NotNull(message = "El stock actual es obligatorio")
        @DecimalMin(value = "0.00", message = "El stock actual no puede ser negativo")
        BigDecimal currentStock,
        @NotNull(message = "El stock minimo es obligatorio")
        @DecimalMin(value = "0.00", message = "El stock minimo no puede ser negativo")
        BigDecimal minimumStock,
        boolean active
) {
}
