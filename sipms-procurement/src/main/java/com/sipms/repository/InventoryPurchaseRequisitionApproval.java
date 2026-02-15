package com.sipms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryPurchaseRequisitionApproval extends JpaRepository<InventoryPurchaseRequisitionApproval,Long> {

    List<InventoryPurchaseRequisitionApproval> findByPurchaseRequisitionIdOrderByApprovalLevelAsc(Long prId);

    List<InventoryPurchaseRequisitionApproval> findByApproverId(Long approverId);

    @Query("SELECT pra FROM InventoryPurchaseRequisitionApproval pra WHERE pra.approverId = :approverId AND " +
            "pra.approvalStatus = 'PENDING' AND pra.purchaseRequisition.isDeleted = false " +
            "ORDER BY pra.purchaseRequisition.priority DESC, pra.purchaseRequisition.prDate ASC")
    List<InventoryPurchaseRequisitionApproval> findPendingApprovalsForUser(@Param("approverId") Long approverId);

    Optional<InventoryPurchaseRequisitionApproval> findByPurchaseRequisitionIdAndApprovalLevel(Long prId, Integer level);

    boolean existsByPurchaseRequisitionIdAndApprovalLevel(Long prId, Integer level);


    long countByApproverIdAndApprovalStatus(Long approverId, String approvalStatus);
}
