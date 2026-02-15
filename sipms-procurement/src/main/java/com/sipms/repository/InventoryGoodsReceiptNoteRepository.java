package com.sipms.repository;

import com.sipms.model.InventoryGoodsReceiptNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InventoryGoodsReceiptNoteRepository extends JpaRepository<InventoryGoodsReceiptNote,Long> , JpaSpecificationExecutor<InventoryGoodsReceiptNote> {

    // Find by GRN number
    Optional<InventoryGoodsReceiptNote> findByGrnNumber(String grnNumber);

    // Check if GRN number exists
    boolean existsByGrnNumber(String grnNumber);

    // Find by PO
    List<InventoryGoodsReceiptNote> findByPurchaseOrderId(Long poId);

    // Find by supplier
    List<InventoryGoodsReceiptNote> findBySupplierId(Long supplierId);

    // Find by status
    List<InventoryGoodsReceiptNote> findByStatus(GRNStatus status);

    Page<InventoryGoodsReceiptNote> findByStatus(GRNStatus status, Pageable pageable);

    // Find pending inspection
    @Query("SELECT grn FROM InventoryGoodsReceiptNote grn WHERE grn.status IN ('DRAFT', 'PENDING_INSPECTION') AND " +
            "grn.isDeleted = false ORDER BY grn.grnDate ASC")
    List<InventoryGoodsReceiptNote> findPendingInspection();

    // Find with discrepancies
    @Query("SELECT grn FROM InventoryGoodsReceiptNote grn WHERE grn.discrepancyNoted = true AND " +
            "grn.isDeleted = false ORDER BY grn.grnDate DESC")
    List<InventoryGoodsReceiptNote> findWithDiscrepancies();

    // Find by date range
    @Query("SELECT grn FROM InventoryGoodsReceiptNote grn WHERE grn.grnDate BETWEEN :startDate AND :endDate AND grn.isDeleted = false")
    List<InventoryGoodsReceiptNote> findByDateRange(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    // Find by received by
    List<InventoryGoodsReceiptNote> findByReceivedBy(Long userId);

    // Find by inspected by
    List<InventoryGoodsReceiptNote> findByInspectedBy(Long userId);

    // Search by GRN number or supplier
    @Query("SELECT grn FROM InventoryGoodsReceiptNote grn WHERE (LOWER(grn.grnNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(grn.supplier.name) LIKE LOWER(CONCAT('%', :search, '%'))) AND grn.isDeleted = false")
    Page<InventoryGoodsReceiptNote> searchGoodsReceiptNotes(@Param("search") String search, Pageable pageable);

    // Find by inspection result
    List<InventoryGoodsReceiptNote> findByInspectionResult(String inspectionResult);

    // Count by status
    long countByStatus(GRNStatus status);

    // Find GRNs ready to post
    @Query("SELECT grn FROM InventoryGoodsReceiptNote grn WHERE grn.status = 'APPROVED' AND grn.postedAt IS NULL AND " +
            "grn.isDeleted = false")
    List<InventoryGoodsReceiptNote> findReadyToPost();

    // Dashboard stats
    @Query("SELECT grn.status, COUNT(grn), SUM(grn.totalAmount) FROM InventoryGoodsReceiptNote grn " +
            "WHERE grn.isDeleted = false GROUP BY grn.status")
    List<Object[]> getDashboardStats();

    // Quality metrics
    @Query("SELECT grn.inspectionResult, COUNT(grn) FROM InventoryGoodsReceiptNote grn " +
            "WHERE grn.grnDate >= :fromDate AND grn.isDeleted = false GROUP BY grn.inspectionResult")
    List<Object[]> getQualityMetrics(@Param("fromDate") LocalDate fromDate);
}

