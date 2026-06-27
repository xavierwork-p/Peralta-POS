package com.peraltapos.accounting;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, UUID> {

    boolean existsBySourceTypeAndSourceId(String sourceType, UUID sourceId);

    Optional<JournalEntry> findBySourceTypeAndSourceId(String sourceType, UUID sourceId);
}
