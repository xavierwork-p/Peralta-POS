package com.peraltapos.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record JournalEntryResponse(
        UUID id,
        String journalCode,
        String journalName,
        String entryNumber,
        LocalDate entryDate,
        String reference,
        String sourceType,
        UUID sourceId,
        JournalEntryStatus status,
        BigDecimal totalDebit,
        BigDecimal totalCredit,
        String notes,
        List<JournalEntryLineResponse> lines
) {
    public static JournalEntryResponse from(JournalEntry entry) {
        return new JournalEntryResponse(
                entry.getId(),
                entry.getJournal().getCode(),
                entry.getJournal().getName(),
                entry.getEntryNumber(),
                entry.getEntryDate(),
                entry.getReference(),
                entry.getSourceType(),
                entry.getSourceId(),
                entry.getStatus(),
                entry.getTotalDebit(),
                entry.getTotalCredit(),
                entry.getNotes(),
                entry.getLines().stream().map(JournalEntryLineResponse::from).toList()
        );
    }
}
