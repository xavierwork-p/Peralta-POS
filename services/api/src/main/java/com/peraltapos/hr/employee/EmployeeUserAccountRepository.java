package com.peraltapos.hr.employee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeUserAccountRepository extends JpaRepository<EmployeeUserAccount, UUID> {

    Optional<EmployeeUserAccount> findByUsernameIgnoreCase(String username);
}
