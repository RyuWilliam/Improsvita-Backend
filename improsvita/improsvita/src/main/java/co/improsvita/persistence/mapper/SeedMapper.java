package co.improsvita.persistence.mapper;

import co.improsvita.domain.model.Seed;
import co.improsvita.persistence.entities.SeedEntity;

public class SeedMapper {

    private SeedMapper() {
    }

    public static Seed toDomain(SeedEntity entity) {
        if (entity == null) return null;
        Seed seed = new Seed();
        seed.setId(entity.getSeedId());
        seed.setName(entity.getName());
        seed.setSupplier(ProviderMapper.toDomain(entity.getSupplier()));
        seed.setQuantity(entity.getQuantity());
        seed.setType(mapType(entity.getType()));
        seed.setAcquisitionDate(entity.getAcquisitionDate());
        seed.setExpirationDate(entity.getExpirationDate());
        seed.setActive(entity.getActive());
        return seed;
    }

    public static SeedEntity toEntity(Seed domain) {
        if (domain == null) return null;
        SeedEntity entity = new SeedEntity();
        entity.setSeedId(domain.getId());
        entity.setName(domain.getName());
        entity.setSupplier(ProviderMapper.toEntity(domain.getSupplier()));
        entity.setQuantity(domain.getQuantity());
        entity.setType(mapType(domain.getType()));
        entity.setAcquisitionDate(domain.getAcquisitionDate());
        entity.setExpirationDate(domain.getExpirationDate());
        entity.setActive(domain.getActive());
        return entity;
    }

    private static Seed.SeedType mapType(co.improsvita.persistence.enums.SeedType entityType) {
        if (entityType == null) return null;
        return switch (entityType) {
            case HYBRID -> Seed.SeedType.HYBRID;
            case TRADITIONAL -> Seed.SeedType.TRADITIONAL;
            case MODIFIED -> Seed.SeedType.MODIFIED;
        };
    }

    private static co.improsvita.persistence.enums.SeedType mapType(Seed.SeedType domainType) {
        if (domainType == null) return null;
        return switch (domainType) {
            case HYBRID -> co.improsvita.persistence.enums.SeedType.HYBRID;
            case TRADITIONAL -> co.improsvita.persistence.enums.SeedType.TRADITIONAL;
            case MODIFIED -> co.improsvita.persistence.enums.SeedType.MODIFIED;
        };
    }
}
