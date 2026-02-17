package com.sipms.model;

import com.sipms.enums.GRNStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "inventory_goods_receipt_note", schema = "procurement")
public class InventoryGoodsReceiptNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Size(max = 50)
    @NotNull
    @Column(name = "grn_number", nullable = false, length = 50)
    private String grnNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_id")
    private InventoryPurchaseOrder purchaseOrder;

    @NotNull
    @ColumnDefault("CURRENT_DATE")
    @Column(name = "grn_date", nullable = false)
    private LocalDate grnDate;

    @Size(max = 100)
    @Column(name = "supplier_invoice_number", length = 100)
    private String supplierInvoiceNumber;

    @Column(name = "supplier_invoice_date")
    private LocalDate supplierInvoiceDate;

    @Size(max = 100)
    @Column(name = "delivery_note_number", length = 100)
    private String deliveryNoteNumber;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Size(max = 50)
    @Column(name = "vehicle_number", length = 50)
    private String vehicleNumber;

    @NotNull
    @Column(name = "received_by", nullable = false)
    private Integer receivedBy;

    @Size(max = 255)
    @Column(name = "warehouse_location")
    private String warehouseLocation;

    @Size(max = 50)
    @NotNull
    @ColumnDefault("'DRAFT'")
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "total_quantity_ordered", precision = 15, scale = 3)
    private BigDecimal totalQuantityOrdered;

    @Column(name = "total_quantity_received", precision = 15, scale = 3)
    private BigDecimal totalQuantityReceived;

    @Column(name = "total_quantity_accepted", precision = 15, scale = 3)
    private BigDecimal totalQuantityAccepted;

    @Column(name = "total_quantity_rejected", precision = 15, scale = 3)
    private BigDecimal totalQuantityRejected;

    @ColumnDefault("0.00")
    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ColumnDefault("false")
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "grn", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventoryGoodsReceiptItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        if (grnDate == null) {
            grnDate = LocalDate.now();
        }
        if (status == null) {
            status = String.valueOf(GRNStatus.DRAFT);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

}