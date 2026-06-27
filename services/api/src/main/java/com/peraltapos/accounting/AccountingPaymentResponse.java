package com.peraltapos.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AccountingPaymentResponse(
        UUID id,
        AccountingPaymentDirection direction,
        UUID receivableId,
        UUID payableId,
        String journalCode,
        String journalName,
        LocalDate paymentDate,
        AccountingPaymentMethod method,
        String partyName,
        BigDecimal amount,
        String reference,
        String notes,
        boolean reconciled
) {
    public static AccountingPaymentResponse from(AccountingPayment payment) {
        return new AccountingPaymentResponse(
                payment.getId(),
                payment.getDirection(),
                payment.getReceivable() == null ? null : payment.getReceivable().getId(),
                payment.getPayable() == null ? null : payment.getPayable().getId(),
                payment.getJournal().getCode(),
                payment.getJournal().getName(),
                payment.getPaymentDate(),
                payment.getMethod(),
                payment.getPartyName(),
                payment.getAmount(),
                payment.getReference(),
                payment.getNotes(),
                payment.isReconciled()
        );
    }
}
