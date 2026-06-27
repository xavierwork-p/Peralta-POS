package com.peraltapos.purchasing.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PurchaseOrderRequest(
        @NotNull(message = "El suplidor es obligatorio")
        UUID supplierId,
        LocalDate expectedDate,
        String notes,
        @NotEmpty(message = "La orden necesita al menos un producto")
        List<@Valid PurchaseOrderItemRequest> items
) {
}
