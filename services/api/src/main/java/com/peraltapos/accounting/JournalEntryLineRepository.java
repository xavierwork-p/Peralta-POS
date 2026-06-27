package com.peraltapos.accounting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface JournalEntryLineRepository extends JpaRepository<JournalEntryLine, UUID> {

    @Query("""
            select new com.peraltapos.accounting.TrialBalanceLineResponse(
                account.code,
                account.name,
                account.accountType,
                account.normalBalance,
                coalesce(sum(line.debit), 0),
                coalesce(sum(line.credit), 0),
                coalesce(sum(line.debit), 0) - coalesce(sum(line.credit), 0)
            )
            from JournalEntryLine line
            join line.account account
            join line.entry entry
            where entry.status = com.peraltapos.accounting.JournalEntryStatus.POSTED
            group by account.code, account.name, account.accountType, account.normalBalance
            order by account.code
            """)
    List<TrialBalanceLineResponse> trialBalance();
}
