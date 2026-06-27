package com.peraltapos.catalog.supplier;

import jakarta.validation.constraints.NotBlank;

public record SupplierRequest(
        @NotBlank(message = "El nombre del suplidor es obligatorio")
        String name,
        String rnc,
        String phone,
        String email,
        String address,
        boolean active
) {
}
