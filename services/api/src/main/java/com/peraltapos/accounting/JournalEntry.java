package com.peraltapos.accounting;

import com.peraltapos.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "accounting_entries")
public class JournalEntry extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_id", nullable = false)
    private AccountingJournal journal;

    @Column(name = "entry_number", nullable = false, unique = true, length = 40)
    private String entryNumber;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(length = 160)
    private String reference;

    @Column(name = "source_type", length = 40)
    private String sourceType;

    @Column(name = "source_id", length = 36)
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID sourceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private JournalEntryStatus status = JournalEntryStatus.POSTED;

    @Column(name = "total_debit", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalDebit = BigDecimal.ZERO;

    @Column(name = "total_credit", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalCredit = BigDecimal.ZERO;

    @Column(length = 600)
    private String notes;

    @OneToMany(mappedBy = "entry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JournalEntryLine> lines = new ArrayList<>();

    public void post(
            AccountingJournal journal,
            String entryNumber,
            LocalDate entryDate,
            String reference,
            String sourceType,
            UUID sourceId,
            String notes,
            List<JournalEntryLine> newLines
    ) {
        this.journal = journal;
        this.entryNumber = entryNumber;
        this.entryDate = entryDate;
        this.reference = reference;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.notes = notes;
        status = JournalEntryStatus.POSTED;

        lines.clear();
        for (JournalEntryLine line : newLines) {
            line.attachTo(this);
            lines.add(line);
        }

        totalDebit = lines.stream()
                .map(JournalEntryLine::getDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalCredit = lines.stream()
                .map(JournalEntryLine::getCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public AccountingJournal getJournal() {
        return journal;
    }

    public String getEntryNumber() {
        return entryNumber;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public String getReference() {
        return reference;
    }

    public String getSourceType() {
        return sourceType;
    }

    public UUID getSourceId() {
        return sourceId;
    }

    public JournalEntryStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalDebit() {
        return totalDebit;
    }

    public BigDecimal getTotalCredit() {
        return totalCredit;
    }

    public String getNotes() {
        return notes;
    }

    public List<JournalEntryLine> getLines() {
        return lines;
    }
}
