package com.peraltapos.accounting;

import java.util.UUID;

public record AccountingJournalResponse(
        UUID id,
        String code,
        String name,
        AccountingJournalType journalType,
        boolean active
) {
    public static AccountingJournalResponse from(AccountingJournal journal) {
        return new AccountingJournalResponse(
                journal.getId(),
                journal.getCode(),
                journal.getName(),
                journal.getJournalType(),
                journal.isActive()
        );
    }
}
