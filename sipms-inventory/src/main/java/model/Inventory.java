package model;

import entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory",
uniqueConstraints = @UniqueConstraint(columnNames = {"product_id","branch_id"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true,exclude = {"product","branch"})
@ToString(exclude = {"product","branch"})
public class Inventory extends BaseEntity {

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
}
