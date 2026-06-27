package com.peraltapos.catalog.supplier;

import com.peraltapos.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "suppliers")
public class Supplier extends BaseEntity {

    @Column(nullable = false, length = 160)
    private String name;

    @Column(length = 30)
    private String rnc;

    @Column(length = 40)
    private String phone;

    @Column(length = 160)
    private String email;

    @Column(length = 300)
    private String address;

    @Column(nullable = false)
    private boolean active = true;

    public void updateFrom(SupplierRequest request) {
        name = request.name();
        rnc = request.rnc();
        phone = request.phone();
        email = request.email();
        address = request.address();
        active = request.active();
    }

    public void deactivate() {
        active = false;
    }

    public String getName() {
        return name;
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

    public boolean isActive() {
        return active;
    }
}
