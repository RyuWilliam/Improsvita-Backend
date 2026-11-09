package co.improsvita.persistence.crud;

import co.improsvita.persistence.entities.SaleEntity;
import co.improsvita.persistence.enums.SaleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SaleJpaRepository extends JpaRepository<SaleEntity, Integer> {

    List<SaleEntity> findBySaleDateBetweenOrderBySaleDateDesc(LocalDate from, LocalDate to);

    List<SaleEntity> findByContact_ContactIdOrderBySaleDateDesc(Integer contactId);

    List<SaleEntity> findByContact_ContactIdAndSaleDateBetweenOrderBySaleDateDesc(
            Integer contactId, LocalDate from, LocalDate to);

    List<SaleEntity> findBySaleTypeOrderBySaleDateDesc(SaleType saleType);

    List<SaleEntity> findBySaleTypeAndSaleDateBetweenOrderBySaleDateDesc(
            SaleType saleType, LocalDate from, LocalDate to);

    List<SaleEntity> findByContact_ContactIdAndSaleTypeOrderBySaleDateDesc(
            Integer contactId, SaleType saleType);

    List<SaleEntity> findByContact_ContactIdAndSaleTypeAndSaleDateBetweenOrderBySaleDateDesc(
            Integer contactId, SaleType saleType, LocalDate from, LocalDate to);

    List<SaleEntity> findBySowing_SowingIdOrderBySaleDateDesc(Integer sowingId);

    Optional<SaleEntity> findByReservation_ReservationId(Integer reservationId);

    /** Cantidad ya vendida (solo ventas activas) de una siembra. */
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM SaleEntity s "
            + "WHERE s.sowing.sowingId = :sowingId AND s.active = true")
    Integer sumActiveQuantityBySowing(@Param("sowingId") Integer sowingId);

    boolean existsBySowing_SowingIdAndActiveTrue(Integer sowingId);
}
