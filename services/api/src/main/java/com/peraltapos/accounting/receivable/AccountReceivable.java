package com.peraltapos.accounting.receivable;

import com.peraltapos.common.domain.BaseEntity;
import com.peraltapos.crm.customer.Customer;
import com.peraltapos.sales.sale.Sale;
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
@Table(name = "accounts_receivable")
public class AccountReceivable extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal balance;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccountReceivableStatus status = AccountReceivableStatus.PENDING;

    public static AccountReceivable fromCreditSale(Customer customer, Sale sale, BigDecimal amount, LocalDate dueDate) {
        AccountReceivable receivable = new AccountReceivable();
        receivable.customer = customer;
        receivable.sale = sale;
        receivable.amount = amount;
        receivable.balance = amount;
        receivable.dueDate = dueDate;
        return receivable;
    }

    public void applyPayment(BigDecimal paymentAmount) {
        if (paymentAmount == null || paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        balance = balance.subtract(paymentAmount);
        if (balance.compareTo(BigDecimal.ZERO) <= 0) {
            balance = BigDecimal.ZERO;
            status = AccountReceivableStatus.PAID;
        } else {
            status = AccountReceivableStatus.PARTIAL;
        }
    }

    public Customer getCustomer() {
        return customer;
    }

    public Sale getSale() {
        return sale;
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

    public AccountReceivableStatus getStatus() {
        return status;
    }
}
