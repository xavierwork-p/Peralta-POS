package com.peraltapos.sales.quote;

import com.peraltapos.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import com.peraltapos.sales.sale.Sale;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quotes")
public class Quote extends BaseEntity {

    @Column(name = "quote_number", nullable = false, unique = true, length = 40)
    private String quoteNumber;

    @Column(name = "customer_name", nullable = false, length = 160)
    private String customerName;

    @Column(name = "customer_fiscal_id", length = 30)
    private String customerFiscalId;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private QuoteStatus status = QuoteStatus.DRAFT;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "tax_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal taxTotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(length = 800)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "converted_sale_id")
    private Sale convertedSale;

    @Column(name = "converted_at")
    private OffsetDateTime convertedAt;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuoteItem> items = new ArrayList<>();

    public void updateHeader(String quoteNumber, QuoteRequest request) {
        this.quoteNumber = quoteNumber;
        customerName = request.customerName();
        customerFiscalId = request.customerFiscalId();
        issueDate = request.issueDate();
        validUntil = request.validUntil();
        status = request.status();
        notes = request.notes();
    }

    public void replaceItems(List<QuoteItem> newItems) {
        items.clear();
        for (QuoteItem item : newItems) {
            item.attachTo(this);
            items.add(item);
        }
        recalculateTotals();
    }

    public void recalculateTotals() {
        subtotal = items.stream()
                .map(QuoteItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        taxTotal = items.stream()
                .map(QuoteItem::getTaxAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        total = subtotal.add(taxTotal);
    }

    public void markConverted(Sale sale) {
        status = QuoteStatus.CONVERTED;
        convertedSale = sale;
        convertedAt = OffsetDateTime.now();
    }

    public String getQuoteNumber() {
        return quoteNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerFiscalId() {
        return customerFiscalId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public QuoteStatus getStatus() {
        return status;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getTaxTotal() {
        return taxTotal;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getNotes() {
        return notes;
    }

    public List<QuoteItem> getItems() {
        return items;
    }

    public Sale getConvertedSale() {
        return convertedSale;
    }

    public OffsetDateTime getConvertedAt() {
        return convertedAt;
    }
}
