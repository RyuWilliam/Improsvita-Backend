package co.improsvita.persistence.mapper;

import co.improsvita.domain.model.SeedTypeCatalog;
import co.improsvita.persistence.entities.SeedTypeEntity;

public class SeedTypeMapper {

    private SeedTypeMapper() {
    }

    public static SeedTypeCatalog toDomain(SeedTypeEntity entity) {
        if (entity == null) return null;
        SeedTypeCatalog seedType = new SeedTypeCatalog();
        seedType.setId(entity.getId());
        seedType.setName(entity.getName());
        seedType.setDescription(entity.getDescription());
        return seedType;
    }

    public static SeedTypeEntity toEntity(SeedTypeCatalog domain) {
        if (domain == null) return null;
        SeedTypeEntity entity = new SeedTypeEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        return entity;
    }
}