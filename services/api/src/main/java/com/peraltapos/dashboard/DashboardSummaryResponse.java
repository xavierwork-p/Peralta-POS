package com.peraltapos.dashboard;

import java.math.BigDecimal;

public record DashboardSummaryResponse(
        long products,
        long lowStockProducts,
        long customers,
        long employees,
        long quotes,
        BigDecimal inventoryCostValue,
        BigDecimal salesToday,
        BigDecimal receivables
) {
}
