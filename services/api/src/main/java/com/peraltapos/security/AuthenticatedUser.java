package com.peraltapos.security;

import com.peraltapos.hr.employee.EmployeeAccessLevel;
import com.peraltapos.hr.employee.EmployeeAccessModule;
import com.peraltapos.hr.employee.EmployeePermission;
import com.peraltapos.hr.employee.EmployeeUserAccount;

import java.security.Principal;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class AuthenticatedUser implements Principal {

    private final UUID accountId;
    private final UUID employeeId;
    private final String username;
    private final String fullName;
    private final Map<EmployeeAccessModule, EmployeeAccessLevel> permissions;

    public AuthenticatedUser(EmployeeUserAccount account) {
        this.accountId = account.getId();
        this.employeeId = account.getEmployee().getId();
        this.username = account.getUsername();
        this.fullName = account.getEmployee().getFullName();
        this.permissions = new EnumMap<>(EmployeeAccessModule.class);
        for (EmployeePermission permission : account.getEmployee().getPermissions()) {
            this.permissions.put(permission.getModule(), permission.getAccessLevel());
        }
    }

    @Override
    public String getName() {
        return username;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean canRead(EmployeeAccessModule module) {
        EmployeeAccessLevel level = permissions.getOrDefault(module, EmployeeAccessLevel.NONE);
        return level == EmployeeAccessLevel.READ || level == EmployeeAccessLevel.WRITE;
    }

    public boolean canWrite(EmployeeAccessModule module) {
        return permissions.getOrDefault(module, EmployeeAccessLevel.NONE) == EmployeeAccessLevel.WRITE;
    }
}
