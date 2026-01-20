package repository;

import entity.BranchStockAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchStockAvailabilityRepository extends JpaRepository<BranchStockAvailability,Long> {
}
