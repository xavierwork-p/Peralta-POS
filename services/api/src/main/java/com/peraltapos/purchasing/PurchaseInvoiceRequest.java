package com.peraltapos.purchasing;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PurchaseInvoiceRequest(
        UUID purchaseOrderId,
        @NotNull(message = "El suplidor es obligatorio")
        UUID supplierId,
        @NotBlank(message = "El numero de factura es obligatorio")
        String documentNumber,
        @NotNull(message = "La fecha de factura es obligatoria")
        LocalDate invoiceDate,
        LocalDate dueDate,
        @NotNull(message = "La condicion de pago es obligatoria")
        PurchasePaymentTerm paymentTerm,
        String notes,
        @NotEmpty(message = "La factura necesita al menos un producto")
        List<@Valid PurchaseInvoiceItemRequest> items
) {
}
