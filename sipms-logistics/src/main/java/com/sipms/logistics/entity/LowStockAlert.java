package com.sipms.logistics.entity;

import com.sipms.inventory.model.Product;
import com.sipms.logistics.enums.AlertSeverity;
import com.sipms.logistics.enums.AlertStatus;
import jakarta.persistence.*;
import lombok.*;
import com.sipms.branch.model.Branch;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "low_stock_alerts",schema= "inventory")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"branch","product"})
public class LowStockAlert extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(nullable = false)
    private Integer currentStock;

    @Column(nullable = false)
    private Integer minimumStockLevel;

    @Column(nullable = false)
    private Integer shortageQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus status;

    @Column(nullable = false)
    private LocalDateTime alertDate;

    private LocalDateTime acknowledgedDate;

    private String acknowledgedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_request_id")
    private StockTransferRequest transferRequest;



}
