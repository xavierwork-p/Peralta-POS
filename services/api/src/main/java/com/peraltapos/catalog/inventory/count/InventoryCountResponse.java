package com.peraltapos.catalog.inventory.count;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record InventoryCountResponse(
        UUID id,
        String countNumber,
        OffsetDateTime countedAt,
        InventoryCountStatus status,
        String notes,
        int productsCounted,
        int productsWithDifference,
        BigDecimal netDifference,
        List<InventoryCountItemResponse> items
) {
    public static InventoryCountResponse from(InventoryCount count) {
        List<InventoryCountItemResponse> items = count.getItems()
                .stream()
                .map(InventoryCountItemResponse::from)
                .toList();
        int differences = (int) count.getItems()
                .stream()
                .filter(item -> item.getDifference().signum() != 0)
                .count();
        BigDecimal netDifference = count.getItems()
                .stream()
                .map(InventoryCountItem::getDifference)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new InventoryCountResponse(
                count.getId(),
                count.getCountNumber(),
                count.getCountedAt(),
                count.getStatus(),
                count.getNotes(),
                items.size(),
                differences,
                netDifference,
                items
        );
    }
}
