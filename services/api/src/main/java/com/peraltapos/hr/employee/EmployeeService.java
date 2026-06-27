package com.peraltapos.hr.employee;

import com.peraltapos.common.web.BusinessException;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeUserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            EmployeeUserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.employeeRepository = employeeRepository;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> list(String search) {
        Sort sort = Sort.by("firstName").ascending().and(Sort.by("lastName").ascending());
        List<Employee> employees = (search == null || search.isBlank())
                ? employeeRepository.findAll(sort)
                : employeeRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrDocumentIdContainingIgnoreCase(search, search, search, sort);

        return employees.stream().map(EmployeeResponse::from).toList();
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        Employee employee = new Employee();
        employee.updateFrom(request);
        syncAccess(employee, request);
        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    @Transactional
    public EmployeeResponse update(UUID id, EmployeeRequest request) {
        Employee employee = findEntity(id);
        employee.updateFrom(request);
        syncAccess(employee, request);
        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public EmployeeResponse get(UUID id) {
        return EmployeeResponse.from(findEntity(id));
    }

    private Employee findEntity(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado"));
    }

    private void syncAccess(Employee employee, EmployeeRequest request) {
        syncUserAccount(employee, request);
        syncPermissions(employee, request);
    }

    private void syncUserAccount(Employee employee, EmployeeRequest request) {
        String username = normalize(request.username());
        if (username == null) {
            employee.setUserAccount(null);
            return;
        }

        validateUniqueUsername(employee, username);
        EmployeeUserAccount account = employee.getUserAccount();
        boolean creatingAccount = account == null;
        if (creatingAccount) {
            account = new EmployeeUserAccount();
        }

        String password = normalize(request.password());
        if (creatingAccount && password == null) {
            throw new BusinessException("La contraseña es obligatoria al crear un usuario");
        }
        if (password != null) {
            account.setPasswordHash(passwordEncoder.encode(password));
            account.setMustChangePassword(true);
        }

        account.setUsername(username);
        account.setActive(request.userActive());
        account.setAllowWebAccess(request.allowWebAccess());
        employee.setUserAccount(account);
    }

    private void validateUniqueUsername(Employee employee, String username) {
        EmployeeUserAccount currentAccount = employee.getUserAccount();
        userAccountRepository.findByUsernameIgnoreCase(username)
                .filter(existing -> currentAccount == null
                        || currentAccount.getId() == null
                        || !existing.getId().equals(currentAccount.getId()))
                .ifPresent(existing -> {
                    throw new BusinessException("Ya existe un empleado con ese usuario");
                });
    }

    private void syncPermissions(Employee employee, EmployeeRequest request) {
        if (request.permissions() == null) {
            return;
        }

        Map<EmployeeAccessModule, EmployeeAccessLevel> levels = new EnumMap<>(EmployeeAccessModule.class);
        request.permissions().stream()
                .filter(permission -> permission.module() != null)
                .forEach(permission -> levels.put(
                        permission.module(),
                        permission.accessLevel() == null ? EmployeeAccessLevel.NONE : permission.accessLevel()
                ));

        List<EmployeePermission> permissions = levels.entrySet().stream()
                .map(entry -> {
                    EmployeePermission permission = new EmployeePermission();
                    permission.setModule(entry.getKey());
                    permission.setAccessLevel(entry.getValue());
                    return permission;
                })
                .toList();
        employee.replacePermissions(permissions);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
