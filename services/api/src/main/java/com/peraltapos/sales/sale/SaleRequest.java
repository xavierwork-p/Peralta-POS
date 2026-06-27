package com.peraltapos.sales.sale;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record SaleRequest(
        UUID customerId,
        @NotBlank(message = "El nombre del cliente es obligatorio")
        String customerName,
        String customerFiscalId,
        @DecimalMin(value = "0.00", message = "El descuento no puede ser negativo")
        BigDecimal discountTotal,
        @Valid
        @NotEmpty(message = "La venta necesita al menos un producto")
        List<SaleItemRequest> items,
        @Valid
        @NotEmpty(message = "La venta necesita al menos un pago")
        List<PaymentRequest> payments
) {
}
