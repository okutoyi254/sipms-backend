package com.sipms.model;

import com.sipms.enums.PRItemStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inventory_purchase_order_item", schema = "procurement")
public class InventoryPurchaseOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pr_item_id")
    private InventoryPurchaseRequisitionItem prItem;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "po_id", nullable = false)
    private InventoryPurchaseOrder purchaseOrder;

    @NotNull
    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    @Column(name = "product_id")
    private Long productId;

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
    @Column(name = "quantity_ordered", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantityOrdered;

    @ColumnDefault("0")
    @Column(name = "quantity_received", precision = 15, scale = 3)
    private BigDecimal quantityReceived;

    @ColumnDefault("0")
    @Column(name = "quantity_returned", precision = 15, scale = 3)
    private BigDecimal quantityReturned;

    @NotNull
    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @ColumnDefault("0.00")
    @Column(name = "discount_percent", precision = 5, scale = 2)
    private BigDecimal discountPercent;

    @ColumnDefault("0.00")
    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount;

    @ColumnDefault("0.00")
    @Column(name = "tax_percent", precision = 5, scale = 2)
    private BigDecimal taxPercent;

    @ColumnDefault("0.00")
    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @ColumnDefault("(((quantity_ordered * unit_price) - COALESCE(discount_amount, (0))) + COALESCE(tax_amount, (0)))")
    @Column(name = "line_total", precision = 15, scale = 2)
    private BigDecimal lineTotal;

    @Column(name = "expected_delivery_date")
    private LocalDate expectedDeliveryDate;

    @Column(name = "specifications", length = Integer.MAX_VALUE)
    private String specifications;

    @Size(max = 50)
    @ColumnDefault("'PENDING'")
    @Column(name = "status", length = 50)
    private String status;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        if (status == null) {
            status = String.valueOf(PRItemStatus.PENDING);
        }
        recalculateLineTotal();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
        recalculateLineTotal();
    }

    public void recalculateLineTotal() {
        if (quantityOrdered != null && unitPrice != null) {
            BigDecimal gross = quantityOrdered.multiply(unitPrice);
            BigDecimal disc = discountAmount != null ? discountAmount : BigDecimal.ZERO;
            BigDecimal tax = taxAmount != null ? taxAmount : BigDecimal.ZERO;
            lineTotal = gross.subtract(disc).add(tax).setScale(2, java.math.RoundingMode.HALF_UP);
        }
    }
}