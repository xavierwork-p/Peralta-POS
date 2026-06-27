package com.peraltapos.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peraltapos.common.web.ApiResponse;
import com.peraltapos.hr.employee.EmployeeAccessLevel;
import com.peraltapos.hr.employee.EmployeeAccessModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class PermissionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    public PermissionFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Optional<RequiredPermission> requiredPermission = resolvePermission(request);
        if (requiredPermission.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            filterChain.doFilter(request, response);
            return;
        }

        RequiredPermission required = requiredPermission.get();
        boolean allowed = required.level() == EmployeeAccessLevel.WRITE
                ? user.canWrite(required.module())
                : user.canRead(required.module());

        if (!allowed) {
            writeForbidden(response, "No tienes permiso para entrar o modificar esta parte del sistema.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Optional<RequiredPermission> resolvePermission(HttpServletRequest request) {
        String path = request.getServletPath();
        if (path == null || path.isBlank()) {
            path = request.getRequestURI();
        }

        if (!path.startsWith("/api/")) {
            return Optional.empty();
        }

        if (path.startsWith("/api/auth/") || path.startsWith("/api/health") || path.equals("/error")) {
            return Optional.empty();
        }

        if (path.startsWith("/api/dashboard")) {
            return Optional.empty();
        }

        if (path.startsWith("/api/reports")) {
            return Optional.of(new RequiredPermission(EmployeeAccessModule.REPORTS, EmployeeAccessLevel.READ));
        }

        if (path.startsWith("/api/company")) {
            return Optional.of(required(EmployeeAccessModule.SETTINGS, request));
        }

        if (path.startsWith("/api/billing") || path.startsWith("/api/accounting")) {
            return Optional.of(required(EmployeeAccessModule.ACCOUNTING, request));
        }

        if (path.startsWith("/api/products")) {
            return Optional.of(required(EmployeeAccessModule.PRODUCTS, request));
        }

        if (path.startsWith("/api/employees")) {
            return Optional.of(required(EmployeeAccessModule.EMPLOYEES, request));
        }

        if (path.startsWith("/api/customers")) {
            return Optional.of(required(EmployeeAccessModule.CUSTOMERS, request));
        }

        if (path.startsWith("/api/quotes")) {
            return Optional.of(required(EmployeeAccessModule.QUOTES, request));
        }

        if (path.startsWith("/api/sales")) {
            EmployeeAccessModule module = isRead(request)
                    ? EmployeeAccessModule.SALES_HISTORY
                    : EmployeeAccessModule.POINT_OF_SALE;
            return Optional.of(required(module, request));
        }

        if (path.startsWith("/api/inventory/movements")) {
            return Optional.of(required(EmployeeAccessModule.MOVEMENTS, request));
        }

        if (path.startsWith("/api/inventory/counts")) {
            return Optional.of(required(EmployeeAccessModule.COUNTS, request));
        }

        if (path.startsWith("/api/purchases/orders")) {
            return Optional.of(required(EmployeeAccessModule.REPLENISHMENT, request));
        }

        if (path.startsWith("/api/purchases/invoices")) {
            return Optional.of(required(EmployeeAccessModule.PURCHASES, request));
        }

        if (path.startsWith("/api/suppliers")) {
            return Optional.of(required(EmployeeAccessModule.SUPPLIERS, request));
        }

        if (path.startsWith("/api/dgii/rnc/import")) {
            return Optional.of(new RequiredPermission(EmployeeAccessModule.SETTINGS, EmployeeAccessLevel.WRITE));
        }

        if (path.startsWith("/api/dgii/rnc") || path.startsWith("/api/taxpayers")) {
            return Optional.of(new RequiredPermission(EmployeeAccessModule.CUSTOMERS, EmployeeAccessLevel.READ));
        }

        return Optional.empty();
    }

    private RequiredPermission required(EmployeeAccessModule module, HttpServletRequest request) {
        return new RequiredPermission(module, isRead(request) ? EmployeeAccessLevel.READ : EmployeeAccessLevel.WRITE);
    }

    private boolean isRead(HttpServletRequest request) {
        return HttpMethod.GET.matches(request.getMethod()) || HttpMethod.HEAD.matches(request.getMethod());
    }

    private void writeForbidden(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), ApiResponse.error(message));
    }

    private record RequiredPermission(EmployeeAccessModule module, EmployeeAccessLevel level) {
    }
}
