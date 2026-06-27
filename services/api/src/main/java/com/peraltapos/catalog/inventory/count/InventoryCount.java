package com.peraltapos.catalog.inventory.count;

import com.peraltapos.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventory_counts")
public class InventoryCount extends BaseEntity {

    @Column(name = "count_number", nullable = false, unique = true, length = 40)
    private String countNumber;

    @Column(name = "counted_at", nullable = false)
    private OffsetDateTime countedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InventoryCountStatus status = InventoryCountStatus.POSTED;

    @Column(length = 500)
    private String notes;

    @OneToMany(mappedBy = "inventoryCount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventoryCountItem> items = new ArrayList<>();

    public void post(String countNumber, String notes, List<InventoryCountItem> newItems) {
        this.countNumber = countNumber;
        countedAt = OffsetDateTime.now();
        this.notes = cleanText(notes);
        items.clear();
        for (InventoryCountItem item : newItems) {
            item.attachTo(this);
            items.add(item);
        }
    }

    private String cleanText(String value) {
        String cleanValue = value == null ? null : value.trim();
        return cleanValue == null || cleanValue.isBlank() ? null : cleanValue;
    }

    public String getCountNumber() {
        return countNumber;
    }

    public OffsetDateTime getCountedAt() {
        return countedAt;
    }

    public InventoryCountStatus getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public List<InventoryCountItem> getItems() {
        return items;
    }
}
