package com.peraltapos.accounting;

import com.peraltapos.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "accounting_entry_lines")
public class JournalEntryLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entry_id", nullable = false)
    private JournalEntry entry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountingAccount account;

    @Column(nullable = false, length = 220)
    private String label;

    @Column(name = "partner_name", length = 160)
    private String partnerName;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal debit = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal credit = BigDecimal.ZERO;

    public static JournalEntryLine debit(AccountingAccount account, String label, String partnerName, BigDecimal amount) {
        JournalEntryLine line = new JournalEntryLine();
        line.account = account;
        line.label = label;
        line.partnerName = partnerName;
        line.debit = amount;
        line.credit = BigDecimal.ZERO;
        return line;
    }

    public static JournalEntryLine credit(AccountingAccount account, String label, String partnerName, BigDecimal amount) {
        JournalEntryLine line = new JournalEntryLine();
        line.account = account;
        line.label = label;
        line.partnerName = partnerName;
        line.debit = BigDecimal.ZERO;
        line.credit = amount;
        return line;
    }

    void attachTo(JournalEntry entry) {
        this.entry = entry;
    }

    public AccountingAccount getAccount() {
        return account;
    }

    public String getLabel() {
        return label;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public BigDecimal getDebit() {
        return debit;
    }

    public BigDecimal getCredit() {
        return credit;
    }
}
