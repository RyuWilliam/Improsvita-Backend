package co.improsvita.persistence.crud;

import co.improsvita.persistence.entities.VisitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VisitJpaRepository extends JpaRepository<VisitEntity, Integer> {
    List<VisitEntity> findBySowing_SowingIdOrderByVisitDateDesc(Integer sowingId);

    Optional<VisitEntity> findFirstBySowing_SowingIdAndActiveTrueOrderByVisitDateDescVisitIdDesc(Integer sowingId);
}
