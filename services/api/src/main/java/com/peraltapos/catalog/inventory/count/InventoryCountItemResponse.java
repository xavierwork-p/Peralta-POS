package com.peraltapos.catalog.inventory.count;

import java.math.BigDecimal;
import java.util.UUID;

public record InventoryCountItemResponse(
        UUID id,
        UUID productId,
        String productSku,
        String productName,
        BigDecimal expectedStock,
        BigDecimal countedStock,
        BigDecimal difference
) {
    public static InventoryCountItemResponse from(InventoryCountItem item) {
        return new InventoryCountItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProductSku(),
                item.getProductName(),
                item.getExpectedStock(),
                item.getCountedStock(),
                item.getDifference()
        );
    }
}
