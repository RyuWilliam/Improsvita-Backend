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
        seed.setType(mapType(entity.getType()));
        seed.setDescription(entity.getDescription());
        seed.setActive(entity.getActive());
        return seed;
    }

    public static SeedEntity toEntity(Seed domain) {
        if (domain == null) return null;
        SeedEntity entity = new SeedEntity();
        entity.setSeedId(domain.getId());
        entity.setName(domain.getName());
        entity.setType(mapType(domain.getType()));
        entity.setDescription(domain.getDescription());
        entity.setActive(domain.getActive());
        return entity;
    }

    private static co.improsvita.domain.model.SeedType mapType(co.improsvita.persistence.enums.SeedType entityType) {
        if (entityType == null) return null;
        return switch (entityType) {
            case HYBRID -> co.improsvita.domain.model.SeedType.HYBRID;
            case TRADITIONAL -> co.improsvita.domain.model.SeedType.TRADITIONAL;
            case MODIFIED -> co.improsvita.domain.model.SeedType.MODIFIED;
        };
    }

    private static co.improsvita.persistence.enums.SeedType mapType(co.improsvita.domain.model.SeedType domainType) {
        if (domainType == null) return null;
        return switch (domainType) {
            case HYBRID -> co.improsvita.persistence.enums.SeedType.HYBRID;
            case TRADITIONAL -> co.improsvita.persistence.enums.SeedType.TRADITIONAL;
            case MODIFIED -> co.improsvita.persistence.enums.SeedType.MODIFIED;
        };
    }
}