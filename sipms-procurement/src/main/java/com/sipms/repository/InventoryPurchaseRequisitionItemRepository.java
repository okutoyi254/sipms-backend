package com.sipms.repository;

import com.sipms.model.InventoryPurchaseRequisitionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InventoryPurchaseRequisitionItemRepository extends JpaRepository<InventoryPurchaseRequisitionItem,Long> {

    List<InventoryPurchaseRequisitionItem> findByPurchaseRequisitionId(Long prId);

    List<InventoryPurchaseRequisitionItem> findByPurchaseRequisitionIdAndStatus(Long prId, PRItemStatus status);

    List<InventoryPurchaseRequisitionItem> findByProductId(Long productId);

    long countByPurchaseRequisitionId(Long prId);

    @Query("SELECT pri FROM InventoryPurchaseRequisitionItem pri WHERE pri.status IN ('APPROVED', 'PARTIALLY_ORDERED') AND " +
            "pri.purchaseRequisition.status = 'APPROVED' AND pri.purchaseRequisition.isDeleted = false")
    List<InventoryPurchaseRequisitionItem> findPendingItems();

    @Query("SELECT pri FROM InventoryPurchaseRequisitionItem pri WHERE pri.productId = :productId AND " +
            "pri.purchaseRequisition.prDate BETWEEN :startDate AND :endDate AND " +
            "pri.purchaseRequisition.isDeleted = false")
    List<InventoryPurchaseRequisitionItem> findByProductAndDateRange(@Param("productId") Long productId,
                                                            @Param("startDate") LocalDate startDate,
                                                            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(pri.quantityRequested) FROM InventoryPurchaseRequisitionItem pri WHERE pri.productId = :productId AND " +
            "pri.purchaseRequisition.status NOT IN ('REJECTED', 'CANCELLED') AND " +
            "pri.purchaseRequisition.isDeleted = false")
    Double sumQuantityRequestedByProduct(@Param("productId") Long productId);
}
