package com.sipms.model;

import com.sipms.enums.PRStatus;
import com.sipms.enums.Priority;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "inventory_purchase_requisition", schema = "procurement")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE procurement.inventory_purchase_requisition SET is_deleted = true WHERE id = ?")
public class InventoryPurchaseRequisition {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @Column(name = "pr_number", nullable = false, unique = true, length = 50)
    private String prNumber; // will be generated in service, not entity

    @Column(name = "pr_date", nullable = false)
    private LocalDate prDate;

    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "requested_by", nullable = false)
    private Integer requestedBy;

    @Column(name="description", nullable = false)
    private String description;

    @Size(max = 20)
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private Priority priority;

    @Size(max = 50)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private PRStatus status;

    @Column(name = "justification", length = Integer.MAX_VALUE)
    private String justification;

    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "rejection_reason", length = Integer.MAX_VALUE)
    private String rejectionReason;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "created_by", nullable = false)
    private Integer createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "purchaseRequisition", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InventoryPurchaseRequisitionItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "purchaseRequisition", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InventoryPurchaseRequisitionApproval> approvals = new ArrayList<>();

    @OneToMany(mappedBy = "pr")
    @Builder.Default
    private List<InventoryPurchaseOrder> purchaseOrders = new ArrayList<>();

    // --------------------------
    // Lifecycle hooks
    // --------------------------

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;

        if (prDate == null) {
            prDate = LocalDate.now();
        }

        if (status == null) {
            status = PRStatus.DRAFT;
        }

        if (priority == null) {
            priority = Priority.MEDIUM;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // --------------------------
    // Business methods
    // --------------------------

    public void addItem(InventoryPurchaseRequisitionItem item) {
        items.add(item);
        item.setPurchaseRequisition(this);
        calculateTotalCost();
    }

    public void removeItem(InventoryPurchaseRequisitionItem item) {
        items.remove(item);
        item.setPurchaseRequisition(null);
        calculateTotalCost();
    }

    public void calculateTotalCost() {
        totalAmount = items.stream()
                .map(InventoryPurchaseRequisitionItem::getEstimatedTotal)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
