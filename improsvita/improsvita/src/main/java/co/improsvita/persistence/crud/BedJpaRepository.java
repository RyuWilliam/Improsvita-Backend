package co.improsvita.persistence.crud;

import co.improsvita.persistence.entities.BedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BedJpaRepository extends JpaRepository<BedEntity, Integer> {
    Optional<BedEntity> findByCode(String code);
}