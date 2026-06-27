package com.peraltapos.accounting;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountingJournalRepository extends JpaRepository<AccountingJournal, UUID> {

    Optional<AccountingJournal> findByCode(String code);

    List<AccountingJournal> findAllByActiveTrueOrderByCodeAsc();
}
