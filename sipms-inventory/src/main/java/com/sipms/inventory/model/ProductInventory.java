package com.sipms.inventory.model;

import com.sipms.branch.model.Branch;
import com.sipms.logistics.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_inventory",
uniqueConstraints = @UniqueConstraint(columnNames = {"product_id","branch_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true,exclude = {"product","branch"})
@ToString(exclude = {"product","branch"})
public class ProductInventory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(nullable = false)
    private Integer quantityOnHand = 0;

    @Column(nullable = false)
    private Integer quantityReserved = 0;

    @Column(nullable = false)
    private Integer quantityAvailable = 0;

    @Column(nullable = false)
    private BigDecimal averageCost;

    @Column(nullable = false)
    private Integer minimumStockLevel = 0;
}
