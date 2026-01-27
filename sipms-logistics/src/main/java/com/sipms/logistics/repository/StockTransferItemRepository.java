package com.sipms.logistics.repository;

import com.sipms.logistics.entity.StockTransferItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockTransferItemRepository extends JpaRepository<StockTransferItem,Long> {
}
