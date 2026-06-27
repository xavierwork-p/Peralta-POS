package com.peraltapos.security;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank(message = "El usuario es obligatorio")
        String username,
        @NotBlank(message = "La contraseña es obligatoria")
        String password,
        String clientChannel
) {
}
