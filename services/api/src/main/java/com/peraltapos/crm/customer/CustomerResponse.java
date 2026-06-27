package com.peraltapos.crm.customer;

import java.math.BigDecimal;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        CustomerType type,
        String fiscalId,
        CustomerFiscalProfile fiscalProfile,
        String phone,
        String email,
        String address,
        BigDecimal creditLimit,
        boolean active
) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getType(),
                customer.getFiscalId(),
                customer.getFiscalProfile(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getAddress(),
                customer.getCreditLimit(),
                customer.isActive()
        );
    }
}
