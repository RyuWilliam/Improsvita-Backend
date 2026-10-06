package co.improsvita.persistence.crud;

import co.improsvita.persistence.entities.SeedMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeedMovementJpaRepository extends JpaRepository<SeedMovementEntity, Integer> {
    List<SeedMovementEntity> findBySeedLot_LotIdOrderByMovementDateAsc(Integer lotId);
    List<SeedMovementEntity> findBySupplier_SupplierId(Integer supplierId);
}