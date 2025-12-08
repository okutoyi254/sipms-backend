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
    private int productQuantity;
    private double unitCost;

    public Product(long productId, String productName, double unitCost) {

        this.productId=productId;
        this.productName=productName;
        this.unitCost=unitCost;
    }
}
