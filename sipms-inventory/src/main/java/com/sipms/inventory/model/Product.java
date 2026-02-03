package com.sipms.inventory.model;

import com.sipms.inventory.enums.ProductStatus;
import com.sipms.logistics.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = true,exclude = {"inventoryRecords","branchProducts","stockMovements"})
@ToString(exclude = {"category","inventoryRecords","branchProducts","stockMovements"})
@Table(name = "products",schema="inventory", uniqueConstraints = {
        @UniqueConstraint(columnNames = "productCode"),
        @UniqueConstraint(columnNames = "product_name")
    },
       indexes = {
           @Index(name = "idx_product_code", columnList = "productCode"),
           @Index(name = "idx_product_name", columnList = "product_name")
       })
public class Product extends BaseEntity {


    @Column(nullable = false, unique = true, length = 50)
    private String productCode;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;


    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductInventory> inventoryRecords = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<BranchProduct> branchProducts = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<StockMovement> stockMovements = new ArrayList<>();

    @Column(nullable = false,precision = 19,scale = 2)
    private BigDecimal standardPrice;

    @Column(nullable = false,precision = 19,scale = 2)
    private BigDecimal standardCost;

    @Column(nullable = false)
    private String unitOfMeasure;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @ElementCollection
    @CollectionTable(name = "product_images",schema = "inventory", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private Set<String> images = new HashSet<>();



}
