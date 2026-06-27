package com.peraltapos.taxpayer;

import java.time.OffsetDateTime;

public record DgiiRncImportSummary(
        long read,
        long inserted,
        long updated,
        long ignored,
        OffsetDateTime updatedAt
) {
}
