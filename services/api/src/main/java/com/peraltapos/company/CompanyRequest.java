package com.peraltapos.company;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CompanyRequest(
        @NotBlank(message = "El nombre legal es obligatorio")
        String name,
        String commercialName,
        String rnc,
        String phone,
        String email,
        String address,
        String logoUrl,
        @NotBlank(message = "La moneda es obligatoria")
        String currencyCode,
        @NotNull(message = "El ITBIS por defecto es obligatorio")
        @DecimalMin(value = "0.00", message = "El ITBIS no puede ser negativo")
        BigDecimal taxRate
) {
}
