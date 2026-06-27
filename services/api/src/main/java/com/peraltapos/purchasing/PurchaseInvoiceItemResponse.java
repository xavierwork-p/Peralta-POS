package com.peraltapos.purchasing;

import java.math.BigDecimal;
import java.util.UUID;

public record PurchaseInvoiceItemResponse(
        UUID id,
        UUID productId,
        String productName,
        BigDecimal quantity,
        BigDecimal unitCost,
        BigDecimal taxRate,
        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal lineTotal
) {
    public static PurchaseInvoiceItemResponse from(PurchaseInvoiceItem item) {
        return new PurchaseInvoiceItemResponse(
                item.getId(),
                item.getProduct().getId(),
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
