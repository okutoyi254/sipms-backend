package com.sipms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "goods_receipt_item", schema = "procurement")
public class GoodsReceiptItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "grn_id", nullable = false)
    private InventoryGoodsReceiptNote grn;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "po_item_id", nullable = false)
    private InventoryPurchaseOrderItem poItem;

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

    @Size(max = 100)
    @Column(name = "batch_number", length = 100)
    private String batchNumber;

    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Size(max = 20)
    @NotNull
    @Column(name = "unit_of_measure", nullable = false, length = 20)
    private String unitOfMeasure;

    @NotNull
    @Column(name = "quantity_ordered", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantityOrdered;

    @NotNull
    @Column(name = "quantity_received", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantityReceived;

    @NotNull
    @Column(name = "quantity_accepted", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantityAccepted;

    @ColumnDefault("0")
    @Column(name = "quantity_rejected", precision = 15, scale = 3)
    private BigDecimal quantityRejected;

    @NotNull
    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @ColumnDefault("(quantity_accepted * unit_price)")
    @Column(name = "line_total", precision = 15, scale = 2)
    private BigDecimal lineTotal;

    @Size(max = 20)
    @Column(name = "condition", length = 20)
    private String condition;

    @Column(name = "rejection_reason", length = Integer.MAX_VALUE)
    private String rejectionReason;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;


}