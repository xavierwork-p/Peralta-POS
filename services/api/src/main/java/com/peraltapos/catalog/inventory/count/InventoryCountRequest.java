package com.peraltapos.catalog.inventory.count;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record InventoryCountRequest(
        String notes,
        @NotEmpty(message = "El conteo necesita al menos un producto")
        List<@Valid InventoryCountItemRequest> items
) {
}
