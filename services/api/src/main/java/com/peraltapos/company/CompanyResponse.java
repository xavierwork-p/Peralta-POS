package com.peraltapos.company;

import java.math.BigDecimal;
import java.util.UUID;

public record CompanyResponse(
        UUID id,
        String name,
        String commercialName,
        String rnc,
        String phone,
        String email,
        String address,
        String logoUrl,
        String currencyCode,
        BigDecimal taxRate
) {
    public static CompanyResponse from(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getCommercialName(),
                company.getRnc(),
                company.getPhone(),
                company.getEmail(),
                company.getAddress(),
                company.getLogoUrl(),
                company.getCurrencyCode(),
                company.getTaxRate()
        );
    }
}
