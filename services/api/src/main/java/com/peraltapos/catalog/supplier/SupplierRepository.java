package com.peraltapos.catalog.supplier;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {

    List<Supplier> findByNameContainingIgnoreCaseOrRncContainingIgnoreCase(String name, String rnc, Sort sort);
}
