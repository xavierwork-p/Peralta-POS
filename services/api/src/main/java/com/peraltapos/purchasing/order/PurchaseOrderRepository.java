package com.peraltapos.purchasing.order;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {

    List<PurchaseOrder> findBySupplier_NameContainingIgnoreCaseOrOrderNumberContainingIgnoreCase(
            String supplierName,
            String orderNumber,
            Sort sort
    );
}
