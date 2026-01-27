package com.sipms.logistics.repository;

import com.sipms.logistics.entity.LowStockAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LowStockAlertRepository extends JpaRepository<LowStockAlert,Long> {
    LowStockAlert findByTransferRequestId(Long id);
}
