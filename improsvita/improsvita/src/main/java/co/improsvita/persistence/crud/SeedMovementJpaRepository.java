package co.improsvita.persistence.crud;

import co.improsvita.persistence.entities.SeedMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeedMovementJpaRepository extends JpaRepository<SeedMovementEntity, Integer> {
    List<SeedMovementEntity> findBySeedLot_LotIdOrderByMovementDateAsc(Integer lotId);
    List<SeedMovementEntity> findBySupplier_SupplierId(Integer supplierId);
    List<SeedMovementEntity> findBySowing_SowingId(Integer sowingId);

    /** Desliga los movimientos de una siembra antes de borrarla (evita romper la FK). */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update SeedMovementEntity m set m.sowing = null where m.sowing.sowingId = :sowingId")
    int detachFromSowing(@Param("sowingId") Integer sowingId);
}