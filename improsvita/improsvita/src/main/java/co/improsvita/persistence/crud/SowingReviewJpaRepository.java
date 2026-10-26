package co.improsvita.persistence.crud;

import co.improsvita.persistence.entities.SowingReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SowingReviewJpaRepository extends JpaRepository<SowingReviewEntity, Integer> {
    List<SowingReviewEntity> findBySowing_SowingIdOrderByReviewDateDescReviewIdDesc(Integer sowingId);
    Optional<SowingReviewEntity> findFirstBySowing_SowingIdOrderByReviewDateDescReviewIdDesc(Integer sowingId);
}