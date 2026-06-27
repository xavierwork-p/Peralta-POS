package com.peraltapos.hr.employee;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    List<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrDocumentIdContainingIgnoreCase(
            String firstName,
            String lastName,
            String documentId,
            Sort sort
    );
}
