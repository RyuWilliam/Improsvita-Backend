package co.improsvita.domain.repository;

import co.improsvita.domain.model.SeedSupplier;

import java.util.List;
import java.util.Optional;

public interface SeedSupplierRepository {
    SeedSupplier save(SeedSupplier seedSupplier);
    Optional<SeedSupplier> findActiveBySeedAndSupplier(Integer seedId, Integer supplierId);
    List<SeedSupplier> getBySeedId(Integer seedId);
    List<SeedSupplier> getBySupplierId(Integer supplierId);
}