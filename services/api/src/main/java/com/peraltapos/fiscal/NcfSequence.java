package com.peraltapos.fiscal;

import com.peraltapos.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "ncf_sequences")
public class NcfSequence extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 40)
    private FiscalDocumentType documentType;

    @Column(nullable = false, length = 10)
    private String prefix;

    @Column(name = "start_number", nullable = false)
    private long startNumber;

    @Column(name = "current_number", nullable = false)
    private long currentNumber;

    @Column(name = "end_number", nullable = false)
    private long endNumber;

    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntil;

    @Column(nullable = false)
    private boolean active = true;

    public String issueNext() {
        String ncf = prefix + String.format("%08d", currentNumber);
        if (currentNumber >= endNumber) {
            active = false;
        } else {
            currentNumber++;
        }
        return ncf;
    }

    public FiscalDocumentType getDocumentType() {
        return documentType;
    }

    public String getPrefix() {
        return prefix;
    }

    public long getCurrentNumber() {
        return currentNumber;
    }

    public long getEndNumber() {
        return endNumber;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public boolean isActive() {
        return active;
    }
}
