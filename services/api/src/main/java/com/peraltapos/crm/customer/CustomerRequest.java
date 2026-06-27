package com.peraltapos.crm.customer;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CustomerRequest(
        @NotBlank(message = "El nombre del cliente es obligatorio")
        String name,
        @NotNull(message = "El tipo de cliente es obligatorio")
        CustomerType type,
        String fiscalId,
        CustomerFiscalProfile fiscalProfile,
        String phone,
        String email,
        String address,
        @NotNull(message = "El limite de credito es obligatorio")
        @DecimalMin(value = "0.00", message = "El limite de credito no puede ser negativo")
        BigDecimal creditLimit,
        boolean active
) {
}
