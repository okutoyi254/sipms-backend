package com.sipms.inventory.model;

import com.sipms.branch.model.Branch;
import com.sipms.logistics.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "branch_products",uniqueConstraints = @UniqueConstraint(columnNames =
        {"product_id","branch_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true,exclude = {"product","branch"})
@ToString(exclude = {"product","branch"})
public class BranchProduct extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "branch_id",nullable = false)
    private Branch branch;

    @Column(nullable = false)
    private Boolean isAvailableAtBranch = true;

    @Column(precision = 19, scale = 2)
    private BigDecimal branchPrice;

    @Column(precision = 19, scale = 2)
    private BigDecimal branchCost;

    @Column(nullable = false)
    private Integer currentStock = 0;

    @Column(nullable = false)
    private Integer reservedStock = 0;

    @Column(nullable = false)
    private Integer availableStock = 0;


}
