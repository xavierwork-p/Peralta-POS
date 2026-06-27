package com.peraltapos.hr.employee;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record EmployeeRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String firstName,
        @NotBlank(message = "El apellido es obligatorio")
        String lastName,
        String documentId,
        @NotBlank(message = "El puesto es obligatorio")
        String position,
        String department,
        String phone,
        String email,
        LocalDate hireDate,
        @NotNull(message = "El salario es obligatorio")
        @DecimalMin(value = "0.00", message = "El salario no puede ser negativo")
        BigDecimal salary,
        @NotNull(message = "La comision es obligatoria")
        @DecimalMin(value = "0.00", message = "La comision no puede ser negativa")
        BigDecimal commissionRate,
        boolean active,
        String username,
        String password,
        boolean userActive,
        boolean allowWebAccess,
        List<EmployeePermissionRequest> permissions
) {
}
