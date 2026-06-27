package com.peraltapos.fiscal;

import java.time.LocalDate;
import java.util.UUID;

public record NcfSequenceResponse(
        UUID id,
        FiscalDocumentType documentType,
        String prefix,
        long currentNumber,
        long endNumber,
        LocalDate validUntil,
        boolean active
) {
    public static NcfSequenceResponse from(NcfSequence sequence) {
        return new NcfSequenceResponse(
                sequence.getId(),
                sequence.getDocumentType(),
                sequence.getPrefix(),
                sequence.getCurrentNumber(),
                sequence.getEndNumber(),
                sequence.getValidUntil(),
                sequence.isActive()
        );
    }
}
