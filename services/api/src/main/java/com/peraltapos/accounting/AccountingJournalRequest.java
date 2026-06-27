package com.peraltapos.accounting;

public record AccountingJournalRequest(
        String code,
        String name,
        AccountingJournalType journalType,
        boolean active
) {
}
