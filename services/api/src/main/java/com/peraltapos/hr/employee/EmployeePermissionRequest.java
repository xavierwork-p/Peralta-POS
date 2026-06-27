package com.peraltapos.hr.employee;

import jakarta.validation.constraints.NotNull;

public record EmployeePermissionRequest(
        @NotNull(message = "El modulo es obligatorio")
        EmployeeAccessModule module,
        @NotNull(message = "El nivel de acceso es obligatorio")
        EmployeeAccessLevel accessLevel
) {
}
