package com.peraltapos.purchasing;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseInvoiceRepository extends JpaRepository<PurchaseInvoice, UUID> {

    Optional<PurchaseInvoice> findBySupplier_IdAndDocumentNumberIgnoreCase(UUID supplierId, String documentNumber);

    List<PurchaseInvoice> findBySupplier_NameContainingIgnoreCaseOrDocumentNumberContainingIgnoreCase(
            String supplierName,
            String documentNumber,
            Sort sort
    );
}
