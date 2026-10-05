package co.improsvita.persistence.mapper;

import co.improsvita.domain.model.Bed;
import co.improsvita.persistence.entities.BedEntity;

public class BedMapper {

    private BedMapper() {
    }

    public static Bed toDomain(BedEntity entity) {
        if (entity == null) return null;
        Bed bed = new Bed();
        bed.setId(entity.getBedId());
        bed.setCode(entity.getCode());
        bed.setMaxCapacity(entity.getMaxCapacity());
        bed.setActive(entity.getActive());
        return bed;
    }

    public static BedEntity toEntity(Bed domain) {
        if (domain == null) return null;
        BedEntity entity = new BedEntity();
        entity.setBedId(domain.getId());
        entity.setCode(domain.getCode());
        entity.setMaxCapacity(domain.getMaxCapacity());
        entity.setActive(domain.getActive());
        return entity;
    }
}