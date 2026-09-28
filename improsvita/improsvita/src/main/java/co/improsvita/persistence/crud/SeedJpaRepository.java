package co.improsvita.persistence.crud;

import co.improsvita.persistence.entities.SeedEntity;
import co.improsvita.persistence.enums.SeedType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeedJpaRepository extends JpaRepository<SeedEntity, Integer> {
    Optional<SeedEntity> findByName(String name);
    void deleteByName(String name);
    List<SeedEntity> findByType(SeedType type);

    @Query("SELECT ss.seed FROM SeedSupplierEntity ss WHERE ss.supplier.supplierId = :supplierId AND ss.active = true")
    List<SeedEntity> findActiveBySupplierId(@Param("supplierId") Integer supplierId);
}