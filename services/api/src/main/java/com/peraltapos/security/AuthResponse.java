package com.peraltapos.security;

import com.peraltapos.hr.employee.EmployeeAccessLevel;
import com.peraltapos.hr.employee.EmployeeAccessModule;
import com.peraltapos.hr.employee.EmployeePermission;
import com.peraltapos.hr.employee.EmployeeUserAccount;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public record AuthResponse(
        String token,
        OffsetDateTime expiresAt,
        UserProfile user
) {
    public record UserProfile(
            UUID employeeId,
            String fullName,
            String username,
            boolean allowWebAccess,
            boolean mustChangePassword,
            List<UserPermission> permissions
    ) {
    }

    public record UserPermission(
            EmployeeAccessModule module,
            EmployeeAccessLevel accessLevel
    ) {
        static UserPermission from(EmployeePermission permission) {
            return new UserPermission(permission.getModule(), permission.getAccessLevel());
        }
    }

    static UserProfile profileFrom(EmployeeUserAccount account) {
        return new UserProfile(
                account.getEmployee().getId(),
                account.getEmployee().getFullName(),
                account.getUsername(),
                account.isAllowWebAccess(),
                account.isMustChangePassword(),
                account.getEmployee().getPermissions().stream()
                        .sorted(Comparator.comparing(permission -> permission.getModule().name()))
                        .map(UserPermission::from)
                        .toList()
        );
    }
}
