package com.peraltapos.purchasing.order;

import java.math.BigDecimal;
import java.util.UUID;

public record PurchaseOrderItemResponse(
        UUID id,
        UUID productId,
        String productSku,
        String productName,
        BigDecimal quantity,
        BigDecimal unitCost,
        BigDecimal taxRate,
        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal lineTotal
) {
    public static PurchaseOrderItemResponse from(PurchaseOrderItem item) {
        return new PurchaseOrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProductSku(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitCost(),
                item.getTaxRate(),
                item.getSubtotal(),
                item.getTaxAmount(),
                item.getLineTotal()
        );
    }
}
