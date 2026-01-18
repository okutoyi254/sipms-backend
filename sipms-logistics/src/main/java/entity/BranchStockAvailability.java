package entity;

import jakarta.persistence.*;
import lombok.*;
import model.Branch;
import model.Product;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "branch_stock_availability_view")
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
