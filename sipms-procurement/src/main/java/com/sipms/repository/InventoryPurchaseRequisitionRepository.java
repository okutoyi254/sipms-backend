package com.sipms.repository;

import com.sipms.model.InventoryPurchaseRequisition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryPurchaseRequisitionRepository extends JpaRepository<InventoryPurchaseRequisition,Long> {
}
