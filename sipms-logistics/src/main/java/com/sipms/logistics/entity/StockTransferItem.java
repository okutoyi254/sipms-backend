package com.sipms.logistics.entity;

import com.sipms.inventory.model.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stock_transfer_items")
@Data
@EqualsAndHashCode(callSuper = true,exclude = {"transferRequest","product"})
@ToString(exclude = {"transferRequest","product"})
public class StockTransferItem extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_request_id", nullable = false)
    private StockTransferRequest transferRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer requestedQuantity;

    private Integer approvedQuantity;

    private Integer shippedQuantity;

    private Integer receivedQuantity;

    private Integer damagedQuantity = 0;

    @Column(length = 100)
    private String batchNumber;

    @Column(precision = 19, scale = 2)
    private BigDecimal unitCost;
}
