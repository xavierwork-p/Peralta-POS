package com.peraltapos.accounting;

public record AccountingAccountRequest(
        String code,
        String name,
        AccountingAccountType accountType,
        NormalBalance normalBalance,
        boolean allowReconciliation,
        boolean active
) {
}
