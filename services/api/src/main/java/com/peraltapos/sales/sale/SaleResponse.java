package com.peraltapos.sales.sale;

import com.peraltapos.fiscal.EcfStatus;
import com.peraltapos.fiscal.FiscalDocumentType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record SaleResponse(
        UUID id,
        String invoiceNumber,
        String ncf,
        FiscalDocumentType fiscalDocumentType,
        UUID sourceQuoteId,
        EcfStatus ecfStatus,
        String ecfTrackId,
        String ecfSecurityCode,
        String customerName,
        String customerFiscalId,
        SaleStatus status,
        BigDecimal subtotal,
        BigDecimal taxTotal,
        BigDecimal discountTotal,
        BigDecimal total,
        OffsetDateTime issuedAt,
        List<SaleItemResponse> items,
        List<PaymentResponse> payments
) {
    public static SaleResponse from(Sale sale) {
        return new SaleResponse(
                sale.getId(),
                sale.getInvoiceNumber(),
                sale.getNcf(),
                sale.getFiscalDocumentType(),
                sale.getSourceQuote() == null ? null : sale.getSourceQuote().getId(),
                sale.getEcfStatus(),
                sale.getEcfTrackId(),
                sale.getEcfSecurityCode(),
                sale.getCustomerName(),
                sale.getCustomerFiscalId(),
                sale.getStatus(),
                sale.getSubtotal(),
                sale.getTaxTotal(),
                sale.getDiscountTotal(),
                sale.getTotal(),
                sale.getIssuedAt(),
                sale.getItems().stream().map(SaleItemResponse::from).toList(),
                sale.getPayments().stream().map(PaymentResponse::from).toList()
        );
    }
}
