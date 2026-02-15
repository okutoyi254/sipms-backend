package com.sipms.repository;

import com.sipms.model.InventoryPurchaseRequisition;
import jakarta.annotation.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public interface PurchaseRequisitionRepository extends JpaRepository<InventoryPurchaseRequisition,Long>,
        JpaSpecificationExecutor<InventoryPurchaseRequisition> {

//    Find by PR number
    Optional<InventoryPurchaseRequisition> findByPrNumber(String prNumber);

    boolean existsByPrNumber(String prNumber);

    List<InventoryPurchaseRequisition> findByStatus(PRStatus status);


    Page<InventoryPurchaseRequisition> findByRequestedBy(Long userId, Pageable pageable);

    List<InventoryPurchaseRequisition> findByRequestedBy(Long userId);


    // Find by department
    List<InventoryPurchaseRequisition> findByDepartmentId(Long departmentId);

    // Find pending approvals
    @Query("SELECT pr FROM InventoryPurchaseRequisition pr WHERE pr.status = 'PENDING_APPROVAL' AND pr.isDeleted = false " +
            "ORDER BY pr.priority DESC, pr.prDate ASC")
    List<InventoryPurchaseRequisition> findPendingApprovals();

    // Find by priority
    List<InventoryPurchaseRequisition> findByPriority(Priority priority);

    // Find urgent requisitions
    @Query("SELECT pr FROM InventoryPurchaseRequisition pr WHERE pr.priority = 'URGENT' AND " +
            "pr.status IN ('DRAFT', 'PENDING_APPROVAL', 'APPROVED') AND pr.isDeleted = false")
    List<InventoryPurchaseRequisition> findUrgentRequisitions();

    // Find overdue requisitions
    @Query("SELECT pr FROM InventoryPurchaseRequisition  pr WHERE pr.requiredDate < :currentDate AND " +
            "pr.status NOT IN ('FULLY_CONVERTED', 'CANCELLED', 'REJECTED') AND pr.isDeleted = false")
    List<InventoryPurchaseRequisition> findOverdueRequisitions(@Param("currentDate") LocalDate currentDate);

    // Find by date range
    @Query("SELECT pr FROM InventoryPurchaseRequisition pr WHERE pr.prDate BETWEEN :startDate AND :endDate AND pr.isDeleted = false")
    List<InventoryPurchaseRequisition> findByDateRange(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    // Find by cost center
    List<InventoryPurchaseRequisition> findByCostCenterId(Long costCenterId);

    // Search by PR number or purpose
    @Query("SELECT pr FROM InventoryPurchaseRequisition pr WHERE (LOWER(pr.prNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(pr.purpose) LIKE LOWER(CONCAT('%', :search, '%'))) AND pr.isDeleted = false")
    Page<InventoryPurchaseRequisition> searchPurchaseRequisitions(@Param("search") String search, Pageable pageable);

    // Count by status
    long countByStatus(PRStatus status);

    // Sum total amount by status
    @Query("SELECT SUM(pr.totalAmount) FROM InventoryPurchaseRequisition pr WHERE pr.status = :status AND pr.isDeleted = false")
    Double sumTotalAmountByStatus(@Param("status") PRStatus status);

    // Find approved PRs not yet converted
    @Query("SELECT pr FROM InventoryPurchaseRequisition pr WHERE pr.status = 'APPROVED' AND pr.isDeleted = false " +
            "ORDER BY pr.priority DESC, pr.requiredDate ASC")
    List<InventoryPurchaseRequisition> findApprovedNotConverted();

    // Dashboard stats
    @Query("SELECT pr.status, COUNT(pr), SUM(pr.totalAmount) FROM InventoryPurchaseRequisition pr " +
            "WHERE pr.isDeleted = false GROUP BY pr.status")
    List<Object[]> getDashboardStats();
}
