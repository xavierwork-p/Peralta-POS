package com.peraltapos.fiscal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FiscalEcfDocumentRepository extends JpaRepository<FiscalEcfDocument, UUID> {

    Optional<FiscalEcfDocument> findBySaleId(UUID saleId);
}
