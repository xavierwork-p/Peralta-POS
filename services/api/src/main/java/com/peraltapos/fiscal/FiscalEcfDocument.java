package com.peraltapos.fiscal;

import com.peraltapos.common.domain.BaseEntity;
import com.peraltapos.sales.sale.Sale;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "fiscal_ecf_documents")
public class FiscalEcfDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private EcfStatus status = EcfStatus.READY_TO_SIGN;

    @Lob
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "unsigned_xml", nullable = false, columnDefinition = "longtext")
    private String unsignedXml;

    @Lob
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "signed_xml", columnDefinition = "longtext")
    private String signedXml;

    @Column(name = "xml_hash", length = 128)
    private String xmlHash;

    @Column(name = "signature_value", length = 512)
    private String signatureValue;

    @Column(name = "track_id", length = 80)
    private String trackId;

    @Column(name = "security_code", length = 80)
    private String securityCode;

    @Lob
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "acknowledgement_xml", columnDefinition = "longtext")
    private String acknowledgementXml;

    @Column(name = "generated_at", nullable = false)
    private OffsetDateTime generatedAt = OffsetDateTime.now();

    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;

    @Column(name = "accepted_at")
    private OffsetDateTime acceptedAt;

    public static FiscalEcfDocument generated(Sale sale, String unsignedXml, String signedXml, String xmlHash, String signatureValue) {
        FiscalEcfDocument document = new FiscalEcfDocument();
        document.sale = sale;
        document.status = EcfStatus.SIGNED;
        document.unsignedXml = unsignedXml;
        document.signedXml = signedXml;
        document.xmlHash = xmlHash;
        document.signatureValue = signatureValue;
        document.generatedAt = OffsetDateTime.now();
        return document;
    }

    public void regenerate(String unsignedXml, String signedXml, String xmlHash, String signatureValue) {
        status = EcfStatus.SIGNED;
        this.unsignedXml = unsignedXml;
        this.signedXml = signedXml;
        this.xmlHash = xmlHash;
        this.signatureValue = signatureValue;
        trackId = null;
        securityCode = null;
        acknowledgementXml = null;
        submittedAt = null;
        acceptedAt = null;
        generatedAt = OffsetDateTime.now();
    }

    public void acceptSimulated(String trackId, String securityCode, String acknowledgementXml) {
        status = EcfStatus.ACCEPTED;
        this.trackId = trackId;
        this.securityCode = securityCode;
        this.acknowledgementXml = acknowledgementXml;
        submittedAt = OffsetDateTime.now();
        acceptedAt = submittedAt;
    }

    public Sale getSale() {
        return sale;
    }

    public EcfStatus getStatus() {
        return status;
    }

    public String getUnsignedXml() {
        return unsignedXml;
    }

    public String getSignedXml() {
        return signedXml;
    }

    public String getXmlHash() {
        return xmlHash;
    }

    public String getSignatureValue() {
        return signatureValue;
    }

    public String getTrackId() {
        return trackId;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getAcknowledgementXml() {
        return acknowledgementXml;
    }

    public OffsetDateTime getGeneratedAt() {
        return generatedAt;
    }

    public OffsetDateTime getSubmittedAt() {
        return submittedAt;
    }

    public OffsetDateTime getAcceptedAt() {
        return acceptedAt;
    }
}
