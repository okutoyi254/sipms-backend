package com.sipms.repository;

import com.sipms.enums.SupplierStatus;
import com.sipms.enums.SupplierType;
import com.sipms.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier,Long>, JpaSpecificationExecutor<Supplier> {

    Optional<Supplier> findBySupplierCode(String supplierCode);

    boolean existsBySupplierCode(String supplierCode);

    List<Supplier>findByStatus(SupplierStatus status);

    @Query("SELECT s FROM Supplier s Where s.status = 'ACTIVE' AND s.isDeleted = false")
    List<Supplier> findAllActiveSuppliers();

    @Query("SELECT s FROM Supplier s WHERE s.preferredSupplier = true AND s.status = 'ACTIVE' AND s.isDeleted = false")
    List<Supplier> findPreferredSuppliers();

    List<Supplier>findSupplierType(SupplierType supplierType);

    @Query("SELECT s FROM Supplier s WHERE s.overallRating >= :minRating AND s.status = 'ACTIVE' AND s.isDeleted = false")
    List<Supplier> findSuppliersByMinimumRating(@Param("minRating") double minRating);

    @Query("SELECT s FROM Supplier s WHERE (LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.supplierCode) LIKE LOWER(CONCAT('%', :search, '%'))) AND s.isDeleted = false")
    Page<Supplier> searchSuppliers(@Param("search") String search, Pageable pageable);

//    Find Suppliers with expiring contracts
    @Query("SELECT s FROM Supplier s WHERE s.contractEndDate BETWEEN :startDate AND :endDate AND s.status = 'ACTIVE' AND s.isDeleted = false")
    List<Supplier> findSuppliersWithExpiringContracts(@Param("startDate") java.time.LocalDate startDate, @Param("endDate") java.time.LocalDate endDate);

//    Count by status
    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.status = :status AND s.isDeleted = false")
    long countByStatus(@Param("status") SupplierStatus status);

//    Update Supplier rating
    @Modifying
    @Query("UPDATE Supplier s SET s.overallRating = :rating,s.updatedAt= :updatedAt WHERE s.id = :id AND s.isDeleted = false")
    void  updateSupplierRating(@Param("id") Long id, @Param("rating") double rating, @Param("updatedAt")LocalDateTime updatedAt);

    @Modifying
    @Query("UPDATE Supplier s SET s.totalOrders=s.totalOrders+1,s.updatedAt= :updatedAt WHERE s.id= :supplierId")
    void incrementOrderStats(
            @Param("supplierId") Long supplierId,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    @Query("SELECT sr.overallRating FROM Supplier sr WHERE sr.id = :supplierId")
    Double calculateAverageRating(@Param("supplierId") Long supplierId);





}
