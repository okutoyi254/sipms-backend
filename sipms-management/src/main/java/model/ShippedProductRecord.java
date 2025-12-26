package model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
public class ShippedProductRecord {

    private long shippingId;
    private String source;
    private String destination;
    private long productId;
    private int quantity;

    @Enumerated(value = EnumType.STRING)
    private SHIPPINGSTATUS shippingstatus;
}
