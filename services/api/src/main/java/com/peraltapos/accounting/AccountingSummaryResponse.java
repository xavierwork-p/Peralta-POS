package com.peraltapos.accounting;

import java.math.BigDecimal;
import java.util.List;

public record AccountingSummaryResponse(
        BigDecimal receivablesBalance,
        BigDecimal payablesBalance,
        BigDecimal netPosition,
        List<AccountingDocumentResponse> receivables,
        List<AccountingDocumentResponse> payables
) {
}
