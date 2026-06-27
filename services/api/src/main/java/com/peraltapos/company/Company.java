package com.peraltapos.company;

import com.peraltapos.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "companies")
public class Company extends BaseEntity {

    @Column(nullable = false, length = 160)
    private String name;

    @Column(name = "commercial_name", length = 160)
    private String commercialName;

    @Column(length = 30)
    private String rnc;

    @Column(length = 40)
    private String phone;

    @Column(length = 160)
    private String email;

    @Column(length = 300)
    private String address;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "currency_code", nullable = false, length = 10)
    private String currencyCode = "DOP";

    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate = new BigDecimal("18.00");

    public void updateFrom(CompanyRequest request) {
        name = request.name();
        commercialName = request.commercialName();
        rnc = request.rnc();
        phone = request.phone();
        email = request.email();
        address = request.address();
        logoUrl = request.logoUrl();
        currencyCode = request.currencyCode() == null || request.currencyCode().isBlank()
                ? "DOP"
                : request.currencyCode().trim().toUpperCase();
        taxRate = request.taxRate();
    }

    public String getName() {
        return name;
    }

    public String getCommercialName() {
        return commercialName;
    }

    public String getRnc() {
        return rnc;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }
}
