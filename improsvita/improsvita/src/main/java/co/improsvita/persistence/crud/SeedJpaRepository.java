package co.improsvita.persistence.crud;

import co.improsvita.persistence.entities.SeedEntity;
import co.improsvita.persistence.entities.SupplierEntity;
import co.improsvita.persistence.enums.SeedType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeedJpaRepository extends JpaRepository<SeedEntity, Integer> {
    Optional<SeedEntity> findByName(String name);
    void deleteByName(String name);
    List<SeedEntity> findBySupplier(SupplierEntity supplier);
    List<SeedEntity> findByType(SeedType type);
    List<SeedEntity> findByQuantityLessThan(Integer quantity);
}
