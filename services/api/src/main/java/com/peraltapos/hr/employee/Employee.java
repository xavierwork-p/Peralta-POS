package com.peraltapos.hr.employee;

import com.peraltapos.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees")
public class Employee extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "document_id", length = 30)
    private String documentId;

    @Column(nullable = false, length = 80)
    private String position;

    @Column(length = 80)
    private String department;

    @Column(length = 40)
    private String phone;

    @Column(length = 160)
    private String email;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(precision = 14, scale = 2)
    private BigDecimal salary = BigDecimal.ZERO;

    @Column(name = "commission_rate", precision = 5, scale = 2)
    private BigDecimal commissionRate = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean active = true;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private EmployeeUserAccount userAccount;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeePermission> permissions = new ArrayList<>();

    public void updateFrom(EmployeeRequest request) {
        firstName = request.firstName();
        lastName = request.lastName();
        documentId = request.documentId();
        position = request.position();
        department = request.department();
        phone = request.phone();
        email = request.email();
        hireDate = request.hireDate();
        salary = request.salary();
        commissionRate = request.commissionRate();
        active = request.active();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getPosition() {
        return position;
    }

    public String getDepartment() {
        return department;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public boolean isActive() {
        return active;
    }

    public EmployeeUserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(EmployeeUserAccount userAccount) {
        this.userAccount = userAccount;
        if (userAccount != null) {
            userAccount.setEmployee(this);
        }
    }

    public List<EmployeePermission> getPermissions() {
        return permissions;
    }

    public void replacePermissions(List<EmployeePermission> permissions) {
        this.permissions.clear();
        permissions.forEach(permission -> {
            permission.setEmployee(this);
            this.permissions.add(permission);
        });
    }
}
