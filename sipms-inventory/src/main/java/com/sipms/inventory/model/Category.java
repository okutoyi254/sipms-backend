package com.sipms.inventory.model;

import com.sipms.logistics.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_categories",uniqueConstraints =@UniqueConstraint(columnNames = "category_name"),
      indexes = {
        @Index(name = "idx_parent_category",columnList ="parent_category_id"),
      },schema = "inventory")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode( callSuper = true,onlyExplicitlyIncluded = true)
public class Category extends BaseEntity {

    @Column(name = "category_code",nullable = false,unique = true,length = 50)
    private String categoryCode;



    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory",cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<Category> subCategories=new ArrayList<>();

    @OneToMany(mappedBy = "category",fetch = FetchType.LAZY)
    private List<Product>products=new ArrayList<>();

    @Column(nullable = false)
    private Boolean isGlobal =true;

    public void addProduct(Product product){
        products.add(product);
        product.setCategory(this);
    }

    public void removeProduct(Product product){
        products.remove(product);
        product.setCategory(null);

    }

    public void addSubCategory(Category subCategory){
        subCategories.add(subCategory);
        subCategory.setParentCategory(this);
    }




}
