package com.peraltapos.sales.quote;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record QuoteRequest(
        @NotBlank(message = "El cliente es obligatorio")
        String customerName,
        String customerFiscalId,
        @NotNull(message = "La fecha de emision es obligatoria")
        LocalDate issueDate,
        @NotNull(message = "La fecha de validez es obligatoria")
        @FutureOrPresent(message = "La cotizacion no puede vencer en una fecha pasada")
        LocalDate validUntil,
        @NotNull(message = "El estado es obligatorio")
        QuoteStatus status,
        String notes,
        @Valid
        @NotEmpty(message = "La cotizacion necesita al menos un producto")
        List<QuoteItemRequest> items
) {
}
