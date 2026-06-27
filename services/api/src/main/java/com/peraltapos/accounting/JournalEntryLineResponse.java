package com.peraltapos.accounting;

import java.math.BigDecimal;
import java.util.UUID;

public record JournalEntryLineResponse(
        UUID id,
        String accountCode,
        String accountName,
        String label,
        String partnerName,
        BigDecimal debit,
        BigDecimal credit
) {
    public static JournalEntryLineResponse from(JournalEntryLine line) {
        return new JournalEntryLineResponse(
                line.getId(),
                line.getAccount().getCode(),
                line.getAccount().getName(),
                line.getLabel(),
                line.getPartnerName(),
                line.getDebit(),
                line.getCredit()
        );
    }
}
