package com.peraltapos.sales.quote;

import java.math.BigDecimal;
import java.util.UUID;

public record QuoteItemResponse(
        UUID id,
        UUID productId,
        String productName,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal taxRate,
        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal lineTotal
) {
    public static QuoteItemResponse from(QuoteItem item) {
        return new QuoteItemResponse(
                item.getId(),
                item.getProduct() == null ? null : item.getProduct().getId(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTaxRate(),
                item.getSubtotal(),
                item.getTaxAmount(),
                item.getLineTotal()
        );
    }
}
