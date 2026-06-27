package com.peraltapos.accounting;

import java.time.LocalDate;
import java.util.List;

public record ManualJournalEntryRequest(
        String journalCode,
        LocalDate entryDate,
        String reference,
        String notes,
        List<ManualJournalEntryLineRequest> lines
) {
}
