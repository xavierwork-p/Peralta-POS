package com.peraltapos.fiscal;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NcfSequenceRepository extends JpaRepository<NcfSequence, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<NcfSequence> findFirstByDocumentTypeAndActiveTrueOrderByValidUntilAsc(FiscalDocumentType documentType);

    List<NcfSequence> findByActiveTrueOrderByDocumentTypeAscValidUntilAsc();
}
