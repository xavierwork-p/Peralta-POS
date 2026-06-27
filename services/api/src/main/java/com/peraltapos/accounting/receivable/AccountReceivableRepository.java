package com.peraltapos.accounting.receivable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

public interface AccountReceivableRepository extends JpaRepository<AccountReceivable, UUID> {

    @Query("select coalesce(sum(account.balance), 0) from AccountReceivable account where account.status in :statuses")
    BigDecimal sumBalanceByStatusIn(@Param("statuses") Collection<AccountReceivableStatus> statuses);
}
