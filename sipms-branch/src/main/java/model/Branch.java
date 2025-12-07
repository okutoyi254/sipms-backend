package model;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Branch {

    private String branchId;
    private String branchName;
    private String branchLocation;
    private List<Product> productList;


}
