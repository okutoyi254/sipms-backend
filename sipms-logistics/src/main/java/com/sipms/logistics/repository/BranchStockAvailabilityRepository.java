package com.sipms.logistics.repository;

import com.sipms.logistics.entity.BranchStockAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchStockAvailabilityRepository extends JpaRepository<BranchStockAvailability,Long> {
}
