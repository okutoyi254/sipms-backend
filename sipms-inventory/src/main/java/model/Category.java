package model;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_categories",uniqueConstraints =@UniqueConstraint(columnNames = "category_name"),
      indexes = {
        @Index(name = "idx_parent_category",columnList ="parent_category_id"),
      })
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    @EqualsAndHashCode.Include
    private Long categoryId;

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

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    @Builder.Default
    private List<Product>products=new ArrayList<>();

    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


}
