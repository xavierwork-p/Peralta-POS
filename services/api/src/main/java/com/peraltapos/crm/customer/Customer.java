package com.peraltapos.crm.customer;

import com.peraltapos.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {

    @Column(nullable = false, length = 160)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CustomerType type = CustomerType.FINAL;

    @Column(name = "fiscal_id", length = 30)
    private String fiscalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "fiscal_profile", nullable = false, length = 40)
    private CustomerFiscalProfile fiscalProfile = CustomerFiscalProfile.STANDARD;

    @Column(length = 40)
    private String phone;

    @Column(length = 160)
    private String email;

    @Column(length = 300)
    private String address;

    @Column(name = "credit_limit", nullable = false, precision = 14, scale = 2)
    private BigDecimal creditLimit = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean active = true;

    public void updateFrom(CustomerRequest request) {
        name = request.name();
        type = request.type();
        fiscalId = request.fiscalId();
        fiscalProfile = request.fiscalProfile() == null ? CustomerFiscalProfile.STANDARD : request.fiscalProfile();
        phone = request.phone();
        email = request.email();
        address = request.address();
        creditLimit = request.creditLimit();
        active = request.active();
    }

    public void deactivate() {
        // Se conserva el registro para no romper facturas, cotizaciones ni cuentas por cobrar historicas.
        active = false;
    }

    public String getName() {
        return name;
    }

    public CustomerType getType() {
        return type;
    }

    public String getFiscalId() {
        return fiscalId;
    }

    public CustomerFiscalProfile getFiscalProfile() {
        return fiscalProfile;
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

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public boolean isActive() {
        return active;
    }
}
