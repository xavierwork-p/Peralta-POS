package com.peraltapos.accounting;

import java.util.UUID;

public record AccountingAccountResponse(
        UUID id,
        String code,
        String name,
        AccountingAccountType accountType,
        NormalBalance normalBalance,
        boolean allowReconciliation,
        boolean active
) {
    public static AccountingAccountResponse from(AccountingAccount account) {
        return new AccountingAccountResponse(
                account.getId(),
                account.getCode(),
                account.getName(),
                account.getAccountType(),
                account.getNormalBalance(),
                account.isAllowReconciliation(),
                account.isActive()
        );
    }
}
