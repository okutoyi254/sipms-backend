package model;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Supplier{

    private Integer supplierId;
    private String supplierName;
    private List<String> items;



}
