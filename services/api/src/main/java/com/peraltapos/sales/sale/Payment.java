package com.peraltapos.sales.sale;

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

@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private PaymentMethod method;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(length = 40)
    private PaymentProcessor processor;

    @Column(length = 120)
    private String reference;

    public static Payment from(PaymentRequest request) {
        Payment payment = new Payment();
        payment.method = request.method();
        payment.amount = request.amount();
        payment.processor = request.processor();
        payment.reference = request.reference();
        return payment;
    }

    void attachTo(Sale sale) {
        this.sale = sale;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentProcessor getProcessor() {
        return processor;
    }

    public String getReference() {
        return reference;
    }
}
