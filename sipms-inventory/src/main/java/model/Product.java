package model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode

public class Product {

    private Long productId;
    private String productName;
    private double unitCost;
}
