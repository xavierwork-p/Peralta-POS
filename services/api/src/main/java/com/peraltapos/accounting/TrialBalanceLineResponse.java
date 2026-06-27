package com.peraltapos.accounting;

import java.math.BigDecimal;

public record TrialBalanceLineResponse(
        String accountCode,
        String accountName,
        AccountingAccountType accountType,
        NormalBalance normalBalance,
        BigDecimal debit,
        BigDecimal credit,
        BigDecimal balance
) {
}
