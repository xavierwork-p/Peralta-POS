package com.peraltapos.catalog.inventory.count;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InventoryCountRepository extends JpaRepository<InventoryCount, UUID> {

    List<InventoryCount> findTop20ByOrderByCountedAtDesc();
}
