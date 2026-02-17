package com.sipms.repository;

import com.sipms.enums.PRStatus;
import com.sipms.model.InventoryPurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InventoryPurchaseOrderRepository extends JpaRepository<InventoryPurchaseOrder,Long>,
        JpaSpecificationExecutor<InventoryPurchaseOrder> {

    // Find by PO number
    Optional<InventoryPurchaseOrder> findByPoNumber(String poNumber);

    // Check if PO number exists
    boolean existsByPoNumber(String poNumber);

    // Find by supplier
    List<InventoryPurchaseOrder> findBySupplierId(Long supplierId);

    Page<InventoryPurchaseOrder> findBySupplierId(Long supplierId, Pageable pageable);

    // Find by status
    List<InventoryPurchaseOrder> findByStatus(PRStatus status);

    Page<InventoryPurchaseOrder> findByStatus(PRStatus status, Pageable pageable);

    // Find by PR
    List<InventoryPurchaseOrder> findByPrId(Long prId);

    // Find active POs for supplier
    @Query("SELECT po FROM InventoryPurchaseOrder po WHERE po.supplier.id = :supplierId AND " +
            "po.status NOT IN ('CLOSED', 'CANCELLED') AND po.isDeleted = false " +
            "ORDER BY po.poDate DESC")
    List<InventoryPurchaseOrder> findActivePOsBySupplier(@Param("supplierId") Long supplierId);

    // Find overdue deliveries
    @Query("SELECT po FROM InventoryPurchaseOrder po WHERE po.deliveryDate < :currentDate AND " +
            "po.status IN ('APPROVED', 'SENT_TO_SUPPLIER', 'ACKNOWLEDGED', 'PARTIALLY_RECEIVED') AND " +
            "po.isDeleted = false")
    List<InventoryPurchaseOrder> findOverdueDeliveries(@Param("currentDate") LocalDate currentDate);

    // Find by date range
    @Query("SELECT po FROM InventoryPurchaseOrder po WHERE po.poDate BETWEEN :startDate AND :endDate AND po.isDeleted = false")
    List<InventoryPurchaseOrder> findByDateRange(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    // Search by PO number or supplier
    @Query("SELECT po FROM InventoryPurchaseOrder po WHERE (LOWER(po.poNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(po.supplier.name) LIKE LOWER(CONCAT('%', :search, '%'))) AND po.isDeleted = false")
    Page<InventoryPurchaseOrder> searchPurchaseOrders(@Param("search") String search, Pageable pageable);

    // Find POs pending receipt
    @Query("SELECT po FROM InventoryPurchaseOrder po WHERE po.status IN ('ACKNOWLEDGED', 'PARTIALLY_RECEIVED') AND " +
            "po.isDeleted = false ORDER BY po.deliveryDate ASC")
    List<InventoryPurchaseOrder> findPendingReceipt();

    // Count by status
    long countByStatus(PRStatus status);

    // Sum total amount by status
    @Query("SELECT SUM(po.totalAmount) FROM InventoryPurchaseOrder po WHERE po.status = :status AND po.isDeleted = false")
    Double sumTotalAmountByStatus(@Param("status") PRStatus status);

    // Find by created by
    List<InventoryPurchaseOrder> findByCreatedBy(Long userId);

    Page<InventoryPurchaseOrder> findByCreatedBy(Long userId, Pageable pageable);

    // Dashboard stats
    @Query("SELECT po.status, COUNT(po), SUM(po.totalAmount) FROM InventoryPurchaseOrder po " +
            "WHERE po.isDeleted = false GROUP BY po.status")
    List<Object[]> getDashboardStats();

    // Supplier performance - on-time delivery
    @Query("SELECT po.supplier.id, COUNT(po), " +
            "SUM(CASE WHEN po.status = 'FULLY_RECEIVED' AND " +
            "(SELECT MAX(grn.grnDate) FROM InventoryGoodsReceiptNote grn WHERE grn.purchaseOrder.id = po.id) <= po.deliveryDate " +
            "THEN 1 ELSE 0 END) as onTimeCount " +
            "FROM InventoryPurchaseOrder po WHERE po.status IN ('FULLY_RECEIVED', 'CLOSED') AND " +
            "po.poDate >= :fromDate AND po.isDeleted = false GROUP BY po.supplier.id")
    List<Object[]> getSupplierOnTimeDeliveryStats(@Param("fromDate") LocalDate fromDate);
}

