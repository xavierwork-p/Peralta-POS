package com.peraltapos.accounting;

import java.math.BigDecimal;

public record ManualJournalEntryLineRequest(
        String accountCode,
        String label,
        String partnerName,
        BigDecimal debit,
        BigDecimal credit
) {
}
