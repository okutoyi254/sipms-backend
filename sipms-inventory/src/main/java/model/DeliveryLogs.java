package model;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "delivery_logs")
public class DeliveryLogs {

    @Id
    private Long id;
    private String supplierName;
    private String supplierEmail;
    private int totalItems;
    private double totalPrice;

    @Enumerated(value = EnumType.STRING)
    private DeliveryStatus deliveryStatus;
}
