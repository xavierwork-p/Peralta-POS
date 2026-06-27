package com.peraltapos.purchasing.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PurchaseOrderItemRequest(
        @NotNull(message = "El producto es obligatorio")
        UUID productId,
        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a cero")
        BigDecimal quantity,
        @NotNull(message = "El costo estimado es obligatorio")
        @DecimalMin(value = "0.00", message = "El costo no puede ser negativo")
        BigDecimal unitCost,
        @NotNull(message = "El ITBIS es obligatorio")
        @DecimalMin(value = "0.00", message = "El ITBIS no puede ser negativo")
        BigDecimal taxRate
) {
}
