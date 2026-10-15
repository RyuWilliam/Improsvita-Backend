package co.improsvita.persistence.crud;

import co.improsvita.persistence.entities.SeedTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeedTypeJpaRepository extends JpaRepository<SeedTypeEntity, Integer> {
    Optional<SeedTypeEntity> findByNameIgnoreCase(String name);
}