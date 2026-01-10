package model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private long shippingId;
    private String source;
    private String destination;
    private long productId;
    private int quantity;

    @Enumerated(value = EnumType.STRING)
    private SHIPPINGSTATUS shippingstatus;
}
