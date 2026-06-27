package com.peraltapos.sales.sale;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull(message = "El metodo de pago es obligatorio")
        PaymentMethod method,
        @NotNull(message = "El monto pagado es obligatorio")
        @DecimalMin(value = "0.01", message = "El pago debe ser mayor a cero")
        BigDecimal amount,
        PaymentProcessor processor,
        String reference
) {
}
