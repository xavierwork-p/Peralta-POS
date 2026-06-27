package com.peraltapos.accounting;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountingAccountRepository extends JpaRepository<AccountingAccount, UUID> {

    Optional<AccountingAccount> findByCode(String code);

    List<AccountingAccount> findAllByActiveTrueOrderByCodeAsc();
}
