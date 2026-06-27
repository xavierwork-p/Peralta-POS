package com.peraltapos.catalog.inventory;

import com.peraltapos.catalog.product.Product;
import com.peraltapos.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "inventory_movements")
public class InventoryMovement extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 40)
    private InventoryMovementType movementType;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal quantity;

    @Column(name = "unit_cost", precision = 14, scale = 2)
    private BigDecimal unitCost;

    @Column(length = 120)
    private String reference;

    @Column(length = 500)
    private String notes;

    public static InventoryMovement create(
            Product product,
            InventoryMovementType movementType,
            BigDecimal quantity,
            BigDecimal unitCost,
            String reference,
            String notes
    ) {
        InventoryMovement movement = new InventoryMovement();
        movement.product = product;
        movement.movementType = movementType;
        movement.quantity = quantity;
        movement.unitCost = unitCost;
        movement.reference = reference;
        movement.notes = notes;
        return movement;
    }

    public Product getProduct() {
        return product;
    }

    public InventoryMovementType getMovementType() {
        return movementType;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public String getReference() {
        return reference;
    }

    public String getNotes() {
        return notes;
    }
}
