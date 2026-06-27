package com.peraltapos.security;

import com.peraltapos.common.web.BusinessException;
import com.peraltapos.hr.employee.EmployeeUserAccount;
import com.peraltapos.hr.employee.EmployeeUserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final EmployeeUserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService tokenService;

    public AuthService(
            EmployeeUserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            AuthTokenService tokenService
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        EmployeeUserAccount account = userAccountRepository.findByUsernameIgnoreCase(request.username().trim())
                .orElseThrow(() -> new BusinessException("Usuario o contraseña incorrectos"));

        if (!account.isActive() || !account.getEmployee().isActive()) {
            throw new BusinessException("Este usuario no esta activo");
        }

        if (isWebLogin(request) && !account.isAllowWebAccess()) {
            throw new BusinessException("Este usuario no tiene permiso para entrar por web");
        }

        if (!passwordEncoder.matches(request.password(), account.getPasswordHash())) {
            throw new BusinessException("Usuario o contraseña incorrectos");
        }

        AuthTokenService.IssuedToken token = tokenService.issue(account.getId());
        return new AuthResponse(token.token(), token.expiresAt(), AuthResponse.profileFrom(account));
    }

    @Transactional(readOnly = true)
    public AuthResponse currentUser(UUID accountId) {
        EmployeeUserAccount account = findActiveAccount(accountId);
        AuthTokenService.IssuedToken token = tokenService.issue(account.getId());
        return new AuthResponse(token.token(), token.expiresAt(), AuthResponse.profileFrom(account));
    }

    @Transactional(readOnly = true)
    public AuthenticatedUser authenticatedUser(UUID accountId) {
        return new AuthenticatedUser(findActiveAccount(accountId));
    }

    @Transactional(readOnly = true)
    public EmployeeUserAccount findActiveAccount(UUID accountId) {
        EmployeeUserAccount account = userAccountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException("Sesion no valida"));
        if (!account.isActive() || !account.getEmployee().isActive()) {
            throw new BusinessException("Este usuario no esta activo");
        }
        return account;
    }
    private boolean isWebLogin(AuthRequest request) {
        return request.clientChannel() == null
                || request.clientChannel().isBlank()
                || "WEB".equalsIgnoreCase(request.clientChannel().trim());
    }
}
