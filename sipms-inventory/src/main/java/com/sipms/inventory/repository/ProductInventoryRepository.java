package com.sipms.inventory.repository;

import com.sipms.inventory.model.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory,Long> {
    List<ProductInventory> findAllByProductId(Long productId);

    Optional<ProductInventory> findByProductIdAndBranchId(Long id, Long id1);
}
