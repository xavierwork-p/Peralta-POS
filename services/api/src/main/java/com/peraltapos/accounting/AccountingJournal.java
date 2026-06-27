package com.peraltapos.accounting;

import com.peraltapos.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounting_journals")
public class AccountingJournal extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "journal_type", nullable = false, length = 40)
    private AccountingJournalType journalType;

    @Column(nullable = false)
    private boolean active = true;

    public static AccountingJournal from(AccountingJournalRequest request) {
        AccountingJournal journal = new AccountingJournal();
        journal.updateFrom(request);
        return journal;
    }

    public void updateFrom(AccountingJournalRequest request) {
        code = clean(request.code()).toUpperCase();
        name = clean(request.name());
        journalType = request.journalType();
        active = request.active();
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public AccountingJournalType getJournalType() {
        return journalType;
    }

    public boolean isActive() {
        return active;
    }
}
