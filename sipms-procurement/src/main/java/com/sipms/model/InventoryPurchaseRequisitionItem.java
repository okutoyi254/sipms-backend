package com.sipms.model;

import com.sipms.enums.PRItemStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inventory_purchase_requisition_item",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"pr_id","line_number"})}, schema = "procurement")
public class InventoryPurchaseRequisitionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pr_id", nullable = false)
    private InventoryPurchaseRequisition purchaseRequisition;

    @NotNull
    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    @Column(name = "product_id")
    private Integer productId;

    @Size(max = 100)
    @Column(name = "product_code", length = 100)
    private String productCode;

    @Size(max = 255)
    @NotNull
    @Column(name = "product_name", nullable = false)
    private String productName;

    @Size(max = 20)
    @NotNull
    @Column(name = "unit_of_measure", nullable = false, length = 20)
    private String unitOfMeasure;

    @NotNull
    @Column(name = "quantity_requested", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantityRequested;

    @Column(name = "quantity_approved", precision = 15, scale = 3)
    private BigDecimal quantityApproved;

    @Column(name = "estimated_unit_price", precision = 15, scale = 2)
    private BigDecimal estimatedUnitPrice;

    @ColumnDefault("(quantity_requested * COALESCE(estimated_unit_price, (0)))")
    @Column(name = "estimated_total", precision = 15, scale = 2)
    private BigDecimal estimatedTotal;

    @Column(name = "required_date")
    private LocalDate requiredDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    @Builder.Default
    private PRItemStatus status = PRItemStatus.PENDING;


    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "prItem")
    @Builder.Default
    private List<InventoryPurchaseOrderItem> purchaseOrderItems = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public BigDecimal calculateEstimatedTotal(){

        if(quantityRequested != null && estimatedUnitPrice !=null){
            return quantityRequested.multiply(estimatedUnitPrice).setScale(2);
        }
        return BigDecimal.ZERO;
    }

}