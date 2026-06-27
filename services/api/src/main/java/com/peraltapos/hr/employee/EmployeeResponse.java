package com.peraltapos.hr.employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public record EmployeeResponse(
        UUID id,
        String fullName,
        String firstName,
        String lastName,
        String documentId,
        String position,
        String department,
        String phone,
        String email,
        LocalDate hireDate,
        BigDecimal salary,
        BigDecimal commissionRate,
        boolean active,
        String username,
        boolean userActive,
        boolean allowWebAccess,
        List<EmployeePermissionResponse> permissions
) {
    public static EmployeeResponse from(Employee employee) {
        EmployeeUserAccount userAccount = employee.getUserAccount();
        return new EmployeeResponse(
                employee.getId(),
                employee.getFullName(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getDocumentId(),
                employee.getPosition(),
                employee.getDepartment(),
                employee.getPhone(),
                employee.getEmail(),
                employee.getHireDate(),
                employee.getSalary(),
                employee.getCommissionRate(),
                employee.isActive(),
                userAccount == null ? null : userAccount.getUsername(),
                userAccount != null && userAccount.isActive(),
                userAccount == null || userAccount.isAllowWebAccess(),
                employee.getPermissions().stream()
                        .sorted(Comparator.comparing(permission -> permission.getModule().name()))
                        .map(EmployeePermissionResponse::from)
                        .toList()
        );
    }
}
