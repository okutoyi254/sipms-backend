package model;

import lombok.*;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Branch {


    private String branchId;
    private String branchName;
    private String branchLocation;
    private List<Product> productList;

    public Branch(String branchId, String branchName, String branchLocation) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.branchLocation = branchLocation;
        this.productList = new LinkedList<>();
    }
}
