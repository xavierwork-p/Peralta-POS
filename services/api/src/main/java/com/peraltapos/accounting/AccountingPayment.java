package com.peraltapos.accounting;

import com.peraltapos.accounting.payable.AccountPayable;
import com.peraltapos.accounting.receivable.AccountReceivable;
import com.peraltapos.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "accounting_payments")
public class AccountingPayment extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccountingPaymentDirection direction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receivable_id")
    private AccountReceivable receivable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payable_id")
    private AccountPayable payable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_id", nullable = false)
    private AccountingJournal journal;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private AccountingPaymentMethod method;

    @Column(name = "party_name", length = 160)
    private String partyName;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(length = 160)
    private String reference;

    @Column(length = 600)
    private String notes;

    @Column(nullable = false)
    private boolean reconciled = true;

    public static AccountingPayment customerPayment(
            AccountReceivable receivable,
            AccountingJournal journal,
            AccountingPaymentRequest request
    ) {
        AccountingPayment payment = basePayment(AccountingPaymentDirection.CUSTOMER, journal, request);
        payment.receivable = receivable;
        payment.partyName = receivable.getCustomer() == null ? "Cliente no registrado" : receivable.getCustomer().getName();
        return payment;
    }

    public static AccountingPayment vendorPayment(
            AccountPayable payable,
            AccountingJournal journal,
            AccountingPaymentRequest request
    ) {
        AccountingPayment payment = basePayment(AccountingPaymentDirection.VENDOR, journal, request);
        payment.payable = payable;
        payment.partyName = payable.getSupplier() == null ? "Suplidor no registrado" : payable.getSupplier().getName();
        return payment;
    }

    private static AccountingPayment basePayment(
            AccountingPaymentDirection direction,
            AccountingJournal journal,
            AccountingPaymentRequest request
    ) {
        AccountingPayment payment = new AccountingPayment();
        payment.direction = direction;
        payment.journal = journal;
        payment.paymentDate = request.paymentDate();
        payment.method = request.method();
        payment.amount = request.amount();
        payment.reference = clean(request.reference());
        payment.notes = clean(request.notes());
        payment.reconciled = true;
        return payment;
    }

    private static String clean(String value) {
        String cleanValue = value == null ? null : value.trim();
        return cleanValue == null || cleanValue.isBlank() ? null : cleanValue;
    }

    public AccountingPaymentDirection getDirection() {
        return direction;
    }

    public AccountReceivable getReceivable() {
        return receivable;
    }

    public AccountPayable getPayable() {
        return payable;
    }

    public AccountingJournal getJournal() {
        return journal;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public AccountingPaymentMethod getMethod() {
        return method;
    }

    public String getPartyName() {
        return partyName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getReference() {
        return reference;
    }

    public String getNotes() {
        return notes;
    }

    public boolean isReconciled() {
        return reconciled;
    }
}
