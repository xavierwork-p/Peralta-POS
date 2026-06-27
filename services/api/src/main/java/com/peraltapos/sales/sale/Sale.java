package com.peraltapos.sales.sale;

import com.peraltapos.common.domain.BaseEntity;
import com.peraltapos.crm.customer.Customer;
import com.peraltapos.fiscal.EcfStatus;
import com.peraltapos.fiscal.FiscalDocumentType;
import com.peraltapos.sales.quote.Quote;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")
public class Sale extends BaseEntity {

    @Column(name = "invoice_number", nullable = false, unique = true, length = 40)
    private String invoiceNumber;

    @Column(length = 30)
    private String ncf;

    @Enumerated(EnumType.STRING)
    @Column(name = "fiscal_document_type", nullable = false, length = 40)
    private FiscalDocumentType fiscalDocumentType = FiscalDocumentType.CONSUMO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_quote_id")
    private Quote sourceQuote;

    @Enumerated(EnumType.STRING)
    @Column(name = "ecf_status", nullable = false, length = 40)
    private EcfStatus ecfStatus = EcfStatus.NOT_SUBMITTED;

    @Column(name = "ecf_track_id", length = 80)
    private String ecfTrackId;

    @Column(name = "ecf_security_code", length = 80)
    private String ecfSecurityCode;

    @Column(name = "ecf_signed_xml_path", length = 500)
    private String ecfSignedXmlPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "customer_name", length = 160)
    private String customerName;

    @Column(name = "customer_fiscal_id", length = 30)
    private String customerFiscalId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SaleStatus status = SaleStatus.ISSUED;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "tax_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal taxTotal = BigDecimal.ZERO;

    @Column(name = "discount_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal discountTotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "issued_at", nullable = false)
    private OffsetDateTime issuedAt;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    public void issue(
            String invoiceNumber,
            String ncf,
            FiscalDocumentType fiscalDocumentType,
            Customer customer,
            SaleRequest request
    ) {
        this.invoiceNumber = invoiceNumber;
        this.ncf = ncf;
        this.fiscalDocumentType = fiscalDocumentType;
        this.customer = customer;
        customerName = request.customerName();
        customerFiscalId = request.customerFiscalId();
        discountTotal = request.discountTotal() == null ? BigDecimal.ZERO : request.discountTotal();
        issuedAt = OffsetDateTime.now();
    }

    public void issueFromQuote(
            String invoiceNumber,
            String ncf,
            FiscalDocumentType fiscalDocumentType,
            Customer customer,
            Quote quote
    ) {
        this.invoiceNumber = invoiceNumber;
        this.ncf = ncf;
        this.fiscalDocumentType = fiscalDocumentType;
        this.sourceQuote = quote;
        this.customer = customer;
        customerName = quote.getCustomerName();
        customerFiscalId = quote.getCustomerFiscalId();
        discountTotal = BigDecimal.ZERO;
        issuedAt = OffsetDateTime.now();
    }

    public void replaceItems(List<SaleItem> newItems) {
        items.clear();
        for (SaleItem item : newItems) {
            item.attachTo(this);
            items.add(item);
        }
        recalculateTotals();
    }

    public void replacePayments(List<Payment> newPayments) {
        payments.clear();
        for (Payment payment : newPayments) {
            payment.attachTo(this);
            payments.add(payment);
        }
    }

    public void recalculateTotals() {
        subtotal = items.stream()
                .map(SaleItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        taxTotal = items.stream()
                .map(SaleItem::getTaxAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        total = subtotal.add(taxTotal).subtract(discountTotal);
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getNcf() {
        return ncf;
    }

    public FiscalDocumentType getFiscalDocumentType() {
        return fiscalDocumentType;
    }

    public Quote getSourceQuote() {
        return sourceQuote;
    }

    public EcfStatus getEcfStatus() {
        return ecfStatus;
    }

    public String getEcfTrackId() {
        return ecfTrackId;
    }

    public String getEcfSecurityCode() {
        return ecfSecurityCode;
    }

    public String getEcfSignedXmlPath() {
        return ecfSignedXmlPath;
    }

    public void markEcfGenerated(EcfStatus status, String signedXmlPath) {
        ecfStatus = status;
        ecfSignedXmlPath = signedXmlPath;
    }

    public void markEcfAccepted(String trackId, String securityCode, String signedXmlPath) {
        ecfStatus = EcfStatus.ACCEPTED;
        ecfTrackId = trackId;
        ecfSecurityCode = securityCode;
        ecfSignedXmlPath = signedXmlPath;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerFiscalId() {
        return customerFiscalId;
    }

    public SaleStatus getStatus() {
        return status;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getTaxTotal() {
        return taxTotal;
    }

    public BigDecimal getDiscountTotal() {
        return discountTotal;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public OffsetDateTime getIssuedAt() {
        return issuedAt;
    }

    public List<SaleItem> getItems() {
        return items;
    }

    public List<Payment> getPayments() {
        return payments;
    }
}
