package co.improsvita.persistence.crud;

import co.improsvita.persistence.entities.SowingEntity;
import co.improsvita.persistence.enums.SowingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SowingJpaRepository extends JpaRepository<SowingEntity, Integer> {
    List<SowingEntity> findBySeedLot_LotId(Integer lotId);
    List<SowingEntity> findByBed_BedId(Integer bedId);
    List<SowingEntity> findByStatus(SowingStatus status);
    List<SowingEntity> findByBed_Code(String code);
}