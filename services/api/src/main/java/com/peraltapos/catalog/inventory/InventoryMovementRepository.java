package com.peraltapos.catalog.inventory;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, UUID>, JpaSpecificationExecutor<InventoryMovement> {

    List<InventoryMovement> findTop30ByOrderByCreatedAtDesc();

    List<InventoryMovement> findByProduct_NameContainingIgnoreCaseOrReferenceContainingIgnoreCase(String productName, String reference, Sort sort);
}
