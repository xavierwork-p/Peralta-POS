package com.peraltapos.catalog.inventory;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record InventoryMovementRequest(
        @NotNull(message = "El producto es obligatorio")
        UUID productId,
        @NotNull(message = "El tipo de movimiento es obligatorio")
        InventoryMovementType movementType,
        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a cero")
        BigDecimal quantity,
        @DecimalMin(value = "0.00", message = "El costo no puede ser negativo")
        BigDecimal unitCost,
        String reference,
        String notes
) {
}
