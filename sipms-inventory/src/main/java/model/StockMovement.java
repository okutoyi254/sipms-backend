package model;

import entity.BaseEntity;
import enums.MovementType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity

@Table(name = "stock_movements")
@EqualsAndHashCode(callSuper = true,exclude = {"product","branch","fromBranch","toBranch"})
@ToString(exclude = {"product","branch","fromBranch","toBranch"})

public class StockMovement extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType movementType;

    @Column(nullable = false)
    private Integer quantity;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_branch_id")
    private Branch fromBranch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_branch_id")
    private Branch toBranch;

    @Column(length = 255)
    private String referenceNumber;

    @Column(length = 500)
    private String remarks;

    @Column(nullable = false)
    private LocalDateTime movementDate;

    @Column(length = 100)
    private String performedBy;

    @Column(nullable = false)
    private BigDecimal unitCost;

    @Column(nullable = false)
    private BigDecimal totalCost;

}
