package com.peraltapos.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peraltapos.common.web.ApiResponse;
import com.peraltapos.common.web.BusinessException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private final AuthTokenService tokenService;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    public AuthTokenFilter(AuthTokenService tokenService, AuthService authService, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearer == null || bearer.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!bearer.startsWith("Bearer ")) {
            writeError(response, HttpStatus.UNAUTHORIZED, "Sesion no valida. Inicia sesion de nuevo.");
            return;
        }

        Optional<UUID> accountId = tokenService.verify(bearer.substring("Bearer ".length()).trim());
        if (accountId.isEmpty()) {
            writeError(response, HttpStatus.UNAUTHORIZED, "Sesion vencida o no valida. Inicia sesion de nuevo.");
            return;
        }

        try {
            AuthenticatedUser user = authService.authenticatedUser(accountId.get());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (BusinessException exception) {
            SecurityContextHolder.clearContext();
            writeError(response, HttpStatus.UNAUTHORIZED, exception.getMessage());
        }
    }

    private void writeError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), ApiResponse.error(message));
    }
}
