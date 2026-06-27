package com.peraltapos.crm.customer;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    List<Customer> findByNameContainingIgnoreCaseOrFiscalIdContainingIgnoreCase(String name, String fiscalId, Sort sort);

    Optional<Customer> findByFiscalId(String fiscalId);

    Optional<Customer> findFirstByNameIgnoreCase(String name);
}
