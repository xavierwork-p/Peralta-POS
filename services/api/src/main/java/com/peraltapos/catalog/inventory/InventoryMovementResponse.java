package com.peraltapos.catalog.inventory;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record InventoryMovementResponse(
        UUID id,
        UUID productId,
        String productSku,
        String productName,
        InventoryMovementType movementType,
        BigDecimal quantity,
        BigDecimal unitCost,
        String reference,
        String notes,
        OffsetDateTime createdAt
) {
    public static InventoryMovementResponse from(InventoryMovement movement) {
        return new InventoryMovementResponse(
                movement.getId(),
                movement.getProduct().getId(),
                movement.getProduct().getSku(),
                movement.getProduct().getName(),
                movement.getMovementType(),
                movement.getQuantity(),
                movement.getUnitCost(),
                movement.getReference(),
                movement.getNotes(),
                movement.getCreatedAt()
        );
    }
}
