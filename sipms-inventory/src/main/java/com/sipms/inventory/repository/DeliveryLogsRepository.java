package com.sipms.inventory.repository;


import com.sipms.inventory.model.SupplierDeliveryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryLogsRepository extends JpaRepository<SupplierDeliveryLog,Long> {

}
