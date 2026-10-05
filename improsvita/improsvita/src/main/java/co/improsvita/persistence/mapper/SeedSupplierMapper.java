package co.improsvita.persistence.mapper;

import co.improsvita.domain.model.SeedSupplier;
import co.improsvita.persistence.entities.SeedEntity;
import co.improsvita.persistence.entities.SeedSupplierEntity;
import co.improsvita.persistence.entities.SupplierEntity;

public class SeedSupplierMapper {

    private SeedSupplierMapper() {
    }

    public static SeedSupplier toDomain(SeedSupplierEntity entity) {
        if (entity == null) return null;
        SeedSupplier bridge = new SeedSupplier();
        bridge.setId(entity.getSeedProviderId());
        bridge.setSeedId(entity.getSeed() != null ? entity.getSeed().getSeedId() : null);
        bridge.setSupplierId(entity.getSupplier() != null ? entity.getSupplier().getSupplierId() : null);
        bridge.setActive(entity.getActive());
        return bridge;
    }

    public static SeedSupplierEntity toEntity(SeedSupplier domain, SeedEntity seed, SupplierEntity supplier) {
        if (domain == null) return null;
        SeedSupplierEntity entity = new SeedSupplierEntity();
        entity.setSeedProviderId(domain.getId());
        entity.setSeed(seed);
        entity.setSupplier(supplier);
        entity.setActive(domain.getActive());
        return entity;
    }
}