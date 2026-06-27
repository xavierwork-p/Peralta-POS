package com.peraltapos.sales.sale;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        PaymentMethod method,
        BigDecimal amount,
        PaymentProcessor processor,
        String reference
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getMethod(),
                payment.getAmount(),
                payment.getProcessor(),
                payment.getReference()
        );
    }
}
