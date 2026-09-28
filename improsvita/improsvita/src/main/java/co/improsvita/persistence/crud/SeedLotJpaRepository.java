package co.improsvita.persistence.crud;

import co.improsvita.persistence.entities.SeedLotEntity;
import co.improsvita.persistence.enums.SeedLotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface SeedLotJpaRepository extends JpaRepository<SeedLotEntity, Integer> {
    List<SeedLotEntity> findBySeed_SeedId(Integer seedId);
    List<SeedLotEntity> findByLocation_LocationId(Integer locationId);
    List<SeedLotEntity> findByStatus(SeedLotStatus status);

    @Query("SELECT COALESCE(SUM(l.availableQuantity), 0) FROM SeedLotEntity l WHERE l.seed.seedId = :seedId")
    BigDecimal sumAvailableBySeedId(@Param("seedId") Integer seedId);
}