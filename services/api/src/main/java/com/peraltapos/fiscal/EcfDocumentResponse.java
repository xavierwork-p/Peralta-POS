package com.peraltapos.fiscal;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EcfDocumentResponse(
        UUID id,
        UUID saleId,
        String invoiceNumber,
        String ncf,
        FiscalDocumentType fiscalDocumentType,
        EcfStatus status,
        String unsignedXml,
        String signedXml,
        String xmlHash,
        String signatureValue,
        String trackId,
        String securityCode,
        String acknowledgementXml,
        OffsetDateTime generatedAt,
        OffsetDateTime submittedAt,
        OffsetDateTime acceptedAt
) {
    public static EcfDocumentResponse from(FiscalEcfDocument document) {
        return new EcfDocumentResponse(
                document.getId(),
                document.getSale().getId(),
                document.getSale().getInvoiceNumber(),
                document.getSale().getNcf(),
                document.getSale().getFiscalDocumentType(),
                document.getStatus(),
                document.getUnsignedXml(),
                document.getSignedXml(),
                document.getXmlHash(),
                document.getSignatureValue(),
                document.getTrackId(),
                document.getSecurityCode(),
                document.getAcknowledgementXml(),
                document.getGeneratedAt(),
                document.getSubmittedAt(),
                document.getAcceptedAt()
        );
    }
}
