package com.peraltapos.purchasing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record PurchaseInvoiceResponse(
        UUID id,
        UUID purchaseOrderId,
        String purchaseOrderNumber,
        UUID supplierId,
        String supplierName,
        String supplierRnc,
        String documentNumber,
        LocalDate invoiceDate,
        LocalDate dueDate,
        PurchasePaymentTerm paymentTerm,
        PurchaseInvoiceStatus status,
        BigDecimal subtotal,
        BigDecimal taxTotal,
        BigDecimal total,
        String notes,
        OffsetDateTime createdAt,
        List<PurchaseInvoiceItemResponse> items
) {
    public static PurchaseInvoiceResponse from(PurchaseInvoice invoice) {
        return new PurchaseInvoiceResponse(
                invoice.getId(),
                invoice.getPurchaseOrder() == null ? null : invoice.getPurchaseOrder().getId(),
                invoice.getPurchaseOrder() == null ? null : invoice.getPurchaseOrder().getOrderNumber(),
                invoice.getSupplier().getId(),
                invoice.getSupplier().getName(),
                invoice.getSupplier().getRnc(),
                invoice.getDocumentNumber(),
                invoice.getInvoiceDate(),
                invoice.getDueDate(),
                invoice.getPaymentTerm(),
                invoice.getStatus(),
                invoice.getSubtotal(),
                invoice.getTaxTotal(),
                invoice.getTotal(),
                invoice.getNotes(),
                invoice.getCreatedAt(),
                invoice.getItems().stream().map(PurchaseInvoiceItemResponse::from).toList()
        );
    }
}
