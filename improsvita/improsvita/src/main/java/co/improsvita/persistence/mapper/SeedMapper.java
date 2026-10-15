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
        seed.setSeedType(SeedTypeMapper.toDomain(entity.getSeedType()));
        seed.setDescription(entity.getDescription());
        seed.setActive(entity.getActive());
        return seed;
    }

    /**
     * No asigna el tipo: SeedRepositoryImpl lo resuelve contra la BD para tener la entidad gestionada.
     */
    public static SeedEntity toEntity(Seed domain) {
        if (domain == null) return null;
        SeedEntity entity = new SeedEntity();
        entity.setSeedId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setActive(domain.getActive());
        return entity;
    }
}