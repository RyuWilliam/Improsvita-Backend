package co.improsvita.persistence.crud;

import co.improsvita.persistence.entities.SeedSupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeedSupplierJpaRepository extends JpaRepository<SeedSupplierEntity, Integer> {
    Optional<SeedSupplierEntity> findBySeed_SeedIdAndSupplier_SupplierIdAndActiveTrue(Integer seedId, Integer supplierId);
    List<SeedSupplierEntity> findBySeed_SeedId(Integer seedId);
    List<SeedSupplierEntity> findBySupplier_SupplierId(Integer supplierId);
}