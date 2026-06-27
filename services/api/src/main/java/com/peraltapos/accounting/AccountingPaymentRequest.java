package com.peraltapos.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountingPaymentRequest(
        BigDecimal amount,
        LocalDate paymentDate,
        AccountingPaymentMethod method,
        String journalCode,
        String reference,
        String notes
) {
}
