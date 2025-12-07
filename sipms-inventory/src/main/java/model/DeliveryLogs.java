package model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
@EqualsAndHashCode
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
