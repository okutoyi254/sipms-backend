package com.sipms.inventory.model;

import com.sipms.branch.model.Branch;
import com.sipms.logistics.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "product_batch", schema = "inventory",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "batch_number"}),
        indexes = {
                @Index(name = "idx_batch_number", columnList = "batch_number"),
                @Index(name = "idx_expiry_date", columnList = "expiry_date"),
                @Index(name = "idx_batch_status", columnList = "status"),
                @Index(name = "idx_product_branch", columnList = "product_id, branch_id")
        }
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"product", "branch"})
@ToString(exclude = {"product", "branch"})
public class ProductBatch extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "batch_number", nullable = false, length = 100)
    private String batchNumber;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantityReserved = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantityAvailable = 0;

    @Column(name = "manufacturing_date")
    private LocalDate manufacturingDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "supplier_reference", length = 255)
    private String supplierReference;

    @Column(name = "purchase_order_number", length = 100)
    private String purchaseOrderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private BatchStatus status = BatchStatus.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String notes;


    public boolean isExpired() {
        if (expiryDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(expiryDate);
    }


    public boolean isExpiringSoon(int daysThreshold) {
        if (expiryDate == null) {
            return false;
        }
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        return expiryDate.isBefore(thresholdDate) || expiryDate.isEqual(thresholdDate);
    }


    public void reserveQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (quantityAvailable < amount) {
            throw new IllegalStateException("Insufficient available quantity in batch");
        }
        this.quantityReserved += amount;
        this.quantityAvailable = this.quantity - this.quantityReserved;
    }


    public void releaseReservedQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (quantityReserved < amount) {
            throw new IllegalStateException("Cannot release more than reserved quantity");
        }
        this.quantityReserved -= amount;
        this.quantityAvailable = this.quantity - this.quantityReserved;
    }


    public void reduceQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (quantity < amount) {
            throw new IllegalStateException("Insufficient quantity in batch");
        }
        this.quantity -= amount;
        this.quantityAvailable = this.quantity - this.quantityReserved;
    }

    public void addQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.quantity += amount;
        this.quantityAvailable = this.quantity - this.quantityReserved;
    }


    public boolean hasAvailableStock() {
        return quantityAvailable > 0 && status == BatchStatus.ACTIVE && !isExpired();
    }

    public Long getDaysUntilExpiry() {
        if (expiryDate == null) {
            return null;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }
}