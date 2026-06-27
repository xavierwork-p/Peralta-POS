package com.peraltapos.catalog.supplier;

import java.util.UUID;

public record SupplierResponse(
        UUID id,
        String name,
        String rnc,
        String phone,
        String email,
        String address,
        boolean active
) {
    public static SupplierResponse from(Supplier supplier) {
        return new SupplierResponse(
                supplier.getId(),
                supplier.getName(),
                supplier.getRnc(),
                supplier.getPhone(),
                supplier.getEmail(),
                supplier.getAddress(),
                supplier.isActive()
        );
    }
}
