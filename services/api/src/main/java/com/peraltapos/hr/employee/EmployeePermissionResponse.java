package com.peraltapos.hr.employee;

public record EmployeePermissionResponse(
        EmployeeAccessModule module,
        EmployeeAccessLevel accessLevel
) {
    public static EmployeePermissionResponse from(EmployeePermission permission) {
        return new EmployeePermissionResponse(
                permission.getModule(),
                permission.getAccessLevel()
        );
    }
}
