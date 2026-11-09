package co.improsvita.persistence.crud;

import co.improsvita.persistence.entities.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SaleJpaRepository
        extends JpaRepository<SaleEntity, Integer>, JpaSpecificationExecutor<SaleEntity> {

    List<SaleEntity> findBySowing_SowingIdOrderBySaleDateDesc(Integer sowingId);

    List<SaleEntity> findByReservation_ReservationId(Integer reservationId);

    /** Cantidad ya vendida (solo ventas activas) de una siembra. */
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM SaleEntity s "
            + "WHERE s.sowing.sowingId = :sowingId AND s.active = true")
    Integer sumActiveQuantityBySowing(@Param("sowingId") Integer sowingId);

    boolean existsBySowing_SowingIdAndActiveTrue(Integer sowingId);
}
