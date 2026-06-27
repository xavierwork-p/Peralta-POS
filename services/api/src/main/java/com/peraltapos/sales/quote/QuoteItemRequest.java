package com.peraltapos.sales.quote;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record QuoteItemRequest(
        UUID productId,
        @NotBlank(message = "El producto es obligatorio")
        String productName,
        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a cero")
        BigDecimal quantity,
        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
        BigDecimal unitPrice,
        @NotNull(message = "El ITBIS es obligatorio")
        @DecimalMin(value = "0.00", message = "El ITBIS no puede ser negativo")
        BigDecimal taxRate
) {
}
