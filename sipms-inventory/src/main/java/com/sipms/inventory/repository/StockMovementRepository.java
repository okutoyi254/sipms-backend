package com.sipms.inventory.repository;

import com.sipms.inventory.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement,Long> {


    List<StockMovement> findByProductIdAndBranchId(Long id, Long id1);
}
