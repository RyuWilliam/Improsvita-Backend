package co.improsvita.persistence.crud;

import co.improsvita.persistence.entities.SowingEntity;
import co.improsvita.persistence.enums.SowingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SowingJpaRepository extends JpaRepository<SowingEntity, Integer> {
    boolean existsByBed_BedId(Integer bedId);
    List<SowingEntity> findBySeedLot_LotId(Integer lotId);
    List<SowingEntity> findByBed_BedId(Integer bedId);
    List<SowingEntity> findByStatus(SowingStatus status);
    List<SowingEntity> findByBed_Code(String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SowingEntity s WHERE s.sowingId = :id")
    Optional<SowingEntity> findByIdForUpdate(@Param("id") Integer id);
}