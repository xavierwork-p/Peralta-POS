package com.peraltapos.catalog.inventory.count;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record InventoryCountItemRequest(
        @NotNull(message = "El producto es obligatorio")
        UUID productId,
        @NotNull(message = "La existencia contada es obligatoria")
        @DecimalMin(value = "0.00", message = "La existencia contada no puede ser negativa")
        BigDecimal countedStock
) {
}
