package com.peraltapos.hr.employee;

import com.peraltapos.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "employee_permissions")
public class EmployeePermission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "module_key", nullable = false, length = 60)
    private EmployeeAccessModule module;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_level", nullable = false, length = 20)
    private EmployeeAccessLevel accessLevel = EmployeeAccessLevel.NONE;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public EmployeeAccessModule getModule() {
        return module;
    }

    public void setModule(EmployeeAccessModule module) {
        this.module = module;
    }

    public EmployeeAccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(EmployeeAccessLevel accessLevel) {
        this.accessLevel = accessLevel == null ? EmployeeAccessLevel.NONE : accessLevel;
    }
}
