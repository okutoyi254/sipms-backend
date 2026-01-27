package com.sipms.logistics.repository;

import com.sipms.logistics.entity.StockTransferRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockTransferRequestRepository extends JpaRepository<StockTransferRequest,Long> {


}
