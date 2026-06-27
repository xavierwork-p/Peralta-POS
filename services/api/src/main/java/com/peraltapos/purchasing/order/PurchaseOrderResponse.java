package com.peraltapos.purchasing.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record PurchaseOrderResponse(
        UUID id,
        UUID supplierId,
        String supplierName,
        String supplierRnc,
        String orderNumber,
        LocalDate orderDate,
        LocalDate expectedDate,
        PurchaseOrderStatus status,
        BigDecimal subtotal,
        BigDecimal taxTotal,
        BigDecimal total,
        String notes,
        OffsetDateTime createdAt,
        List<PurchaseOrderItemResponse> items
) {
    public static PurchaseOrderResponse from(PurchaseOrder order) {
        return new PurchaseOrderResponse(
                order.getId(),
                order.getSupplier().getId(),
                order.getSupplier().getName(),
                order.getSupplier().getRnc(),
                order.getOrderNumber(),
                order.getOrderDate(),
                order.getExpectedDate(),
                order.getStatus(),
                order.getSubtotal(),
                order.getTaxTotal(),
                order.getTotal(),
                order.getNotes(),
                order.getCreatedAt(),
                order.getItems().stream().map(PurchaseOrderItemResponse::from).toList()
        );
    }
}
