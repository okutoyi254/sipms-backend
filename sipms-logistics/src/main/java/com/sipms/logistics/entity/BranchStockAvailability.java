package com.sipms.logistics.entity;

import com.sipms.inventory.model.Product;
import jakarta.persistence.*;
import lombok.*;
import com.sipms.branch.model.Branch;


import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "branch_stock_availability_view",schema="inventory")
@Data

public class BranchStockAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    private Integer availableQuantity;

    private Integer reservedQuantity;

    private BigDecimal averageCost;

    @Transient
    public boolean canSupply(Integer requestedQuantity) {
        return availableQuantity >= requestedQuantity;
    }
}
