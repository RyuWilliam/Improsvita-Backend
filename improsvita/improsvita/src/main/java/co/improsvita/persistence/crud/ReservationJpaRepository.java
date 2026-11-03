package co.improsvita.persistence.crud;

import co.improsvita.persistence.entities.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, Integer> {
    List<ReservationEntity> findBySowing_SowingIdOrderByReservationDateDesc(Integer sowingId);
    List<ReservationEntity> findByClient_ClientIdOrderByReservationDateDesc(Integer clientId);
}
