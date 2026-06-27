package com.peraltapos.sales.sale;

import java.math.BigDecimal;
import java.util.UUID;

public record SaleItemResponse(
        UUID id,
        String productName,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal taxRate,
        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal lineTotal
) {
    public static SaleItemResponse from(SaleItem item) {
        return new SaleItemResponse(
                item.getId(),
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
