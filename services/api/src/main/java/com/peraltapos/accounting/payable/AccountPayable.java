package com.peraltapos.accounting.payable;

import com.peraltapos.catalog.supplier.Supplier;
import com.peraltapos.common.domain.BaseEntity;
import com.peraltapos.purchasing.PurchaseInvoice;
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
@Table(name = "accounts_payable")
public class AccountPayable extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_invoice_id")
    private PurchaseInvoice purchaseInvoice;

    @Column(name = "document_number", length = 80)
    private String documentNumber;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal balance;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccountPayableStatus status = AccountPayableStatus.PENDING;

    public static AccountPayable fromPurchaseInvoice(PurchaseInvoice invoice) {
        AccountPayable payable = new AccountPayable();
        payable.supplier = invoice.getSupplier();
        payable.purchaseInvoice = invoice;
        payable.documentNumber = invoice.getDocumentNumber();
        payable.amount = invoice.getTotal();
        payable.balance = invoice.getTotal();
        payable.dueDate = invoice.getDueDate();
        return payable;
    }

    public void applyPayment(BigDecimal paymentAmount) {
        if (paymentAmount == null || paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        balance = balance.subtract(paymentAmount);
        if (balance.compareTo(BigDecimal.ZERO) <= 0) {
            balance = BigDecimal.ZERO;
            status = AccountPayableStatus.PAID;
        } else {
            status = AccountPayableStatus.PARTIAL;
        }
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public PurchaseInvoice getPurchaseInvoice() {
        return purchaseInvoice;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public AccountPayableStatus getStatus() {
        return status;
    }
}
