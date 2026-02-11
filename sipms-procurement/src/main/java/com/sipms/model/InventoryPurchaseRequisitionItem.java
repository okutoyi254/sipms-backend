package com.sipms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "inventory_purchase_requisition_item", schema = "procurement")
public class InventoryPurchaseRequisitionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

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


}