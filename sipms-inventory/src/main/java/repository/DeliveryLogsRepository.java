package repository;

import model.DeliveryLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryLogsRepository extends JpaRepository<DeliveryLogs,Long> {

}
