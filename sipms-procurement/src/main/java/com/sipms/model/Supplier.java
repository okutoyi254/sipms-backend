package com.sipms.model;

import com.sipms.enums.SupplierStatus;
import com.sipms.enums.SupplierType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@Entity
@Table(name = "supplier", schema = "procurement")
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE supplier SET is_deleted = true WHERE id = ?")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Column(name = "supplier_code", nullable = false, length = 50)
    private String supplierCode;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 100)
    @Column(name = "registration_number", length = 100)
    private String registrationNumber;

    @Size(max = 100)
    @Column(name = "tax_id", length = 100)
    private String taxId;

    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @Size(max = 50)
    @Column(name = "phone", length = 50)
    private String phone;

    @Size(max = 255)
    @Column(name = "website")
    private String website;

    @Column(name = "address", length = Integer.MAX_VALUE)
    private String address;

    @Size(max = 100)
    @Column(name = "city", length = 100)
    private String city;

    @Size(max = 50)
    @NotNull
    @Column(name = "supplier_type", nullable = false, length = 50)
    private SupplierType supplierType;

    @Size(max = 50)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private SupplierStatus status= SupplierStatus.PENDING_APPROVAL;

    @ColumnDefault("0")
    @Column(name = "total_orders")
    private Integer totalOrders;

    @ColumnDefault("false")
    @Column(name = "preferred_supplier")
    private Boolean preferredSupplier;

    @Column(name = "contract_start_date")
    private LocalDate contractStartDate;

    @Column(name = "contract_end_date")
    private LocalDate contractEndDate;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ColumnDefault("false")
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Size(max = 255)
    @NotNull
    @Column(name = "contact_person", nullable = false)
    private String contactPerson;

    @Size(max = 100)
    @Column(name = "designation", length = 100)
    private String designation;

    @Column(name = "quality_rating")
    private Integer qualityRating;

    @Column(name = "delivery_rating")
    private Integer deliveryRating;

    @Column(name = "price_rating")
    private Integer priceRating;

    @Column(name = "service_rating")
    private Integer serviceRating;

    @ColumnDefault("0.00")
    @Column(name = "overall_rating", precision = 5, scale = 2)
    private double overallRating;

    @Column(name = "comments", length = Integer.MAX_VALUE)
    private String comments;


    @PostPersist
    protected void onPostPersist() {
        if (supplierCode == null || supplierCode.isBlank()) {
            supplierCode = generateSupplierCode();
        }
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
        // Keep overallRating in sync whenever ratings change
        overallRating = calculateOverallRating();
    }

    private String generateSupplierCode() {
        // Implementation: SUP-YYYY-XXXXXX
        // Should use sequence or repository method
        return "SUP-" + LocalDate.now().getYear() + "-" + String.format("%06d", id);
    }

    @OneToMany(mappedBy = "supplier")
    @Builder.Default
    private List<InventoryPurchaseOrder> purchaseOrders = new ArrayList<>();

    // Calculate overall rating in Java if needed
    public double calculateOverallRating() {
        int count = 0;
        int sum = 0;

        if (qualityRating != null)  { sum += qualityRating;  count++; }
        if (deliveryRating != null) { sum += deliveryRating; count++; }
        if (priceRating != null)    { sum += priceRating;    count++; }
        if (serviceRating != null)  { sum += serviceRating;  count++; }

        return count > 0 ? (double) sum / count : 0.0;
    }
}