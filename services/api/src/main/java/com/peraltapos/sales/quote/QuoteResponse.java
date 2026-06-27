package com.peraltapos.sales.quote;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record QuoteResponse(
        UUID id,
        String quoteNumber,
        String customerName,
        String customerFiscalId,
        LocalDate issueDate,
        LocalDate validUntil,
        QuoteStatus status,
        BigDecimal subtotal,
        BigDecimal taxTotal,
        BigDecimal total,
        String notes,
        UUID convertedSaleId,
        OffsetDateTime convertedAt,
        List<QuoteItemResponse> items
) {
    public static QuoteResponse from(Quote quote) {
        return new QuoteResponse(
                quote.getId(),
                quote.getQuoteNumber(),
                quote.getCustomerName(),
                quote.getCustomerFiscalId(),
                quote.getIssueDate(),
                quote.getValidUntil(),
                quote.getStatus(),
                quote.getSubtotal(),
                quote.getTaxTotal(),
                quote.getTotal(),
                quote.getNotes(),
                quote.getConvertedSale() == null ? null : quote.getConvertedSale().getId(),
                quote.getConvertedAt(),
                quote.getItems().stream().map(QuoteItemResponse::from).toList()
        );
    }
}
