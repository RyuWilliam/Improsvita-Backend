package co.improsvita.persistence.mapper;

import co.improsvita.domain.model.Sowing;
import co.improsvita.persistence.entities.BedEntity;
import co.improsvita.persistence.entities.SeedLotEntity;
import co.improsvita.persistence.entities.SowingEntity;

public class SowingMapper {

    private SowingMapper() {
    }

    public static Sowing toDomain(SowingEntity entity) {
        if (entity == null) return null;
        Sowing sowing = new Sowing();
        sowing.setId(entity.getSowingId());
        sowing.setLotId(entity.getSeedLot() != null ? entity.getSeedLot().getLotId() : null);
        sowing.setBedId(entity.getBed() != null ? entity.getBed().getBedId() : null);
        sowing.setQuantitySown(entity.getQuantitySown());
        sowing.setGerminatedQuantity(entity.getGerminatedQuantity());
        sowing.setSowingDate(entity.getSowingDate());
        sowing.setExpectedGerminationDate(entity.getExpectedGerminationDate());
        sowing.setStatus(mapStatus(entity.getStatus()));
        sowing.setNotes(entity.getNotes());
        sowing.setActive(entity.getActive());
        return sowing;
    }

    public static SowingEntity toEntity(Sowing domain, SeedLotEntity lot, BedEntity bed) {
        if (domain == null) return null;
        SowingEntity entity = new SowingEntity();
        entity.setSowingId(domain.getId());
        entity.setSeedLot(lot);
        entity.setBed(bed);
        entity.setQuantitySown(domain.getQuantitySown());
        entity.setGerminatedQuantity(domain.getGerminatedQuantity());
        entity.setSowingDate(domain.getSowingDate());
        entity.setExpectedGerminationDate(domain.getExpectedGerminationDate());
        entity.setStatus(mapStatus(domain.getStatus()));
        entity.setNotes(domain.getNotes());
        entity.setActive(domain.getActive());
        return entity;
    }

    private static co.improsvita.domain.model.SowingStatus mapStatus(co.improsvita.persistence.enums.SowingStatus entityStatus) {
        if (entityStatus == null) return null;
        return switch (entityStatus) {
            case PLANNED -> co.improsvita.domain.model.SowingStatus.PLANNED;
            case IN_PROGRESS -> co.improsvita.domain.model.SowingStatus.IN_PROGRESS;
            case COMPLETED -> co.improsvita.domain.model.SowingStatus.COMPLETED;
            case FAILED -> co.improsvita.domain.model.SowingStatus.FAILED;
            case CANCELLED -> co.improsvita.domain.model.SowingStatus.CANCELLED;
        };
    }

    private static co.improsvita.persistence.enums.SowingStatus mapStatus(co.improsvita.domain.model.SowingStatus domainStatus) {
        if (domainStatus == null) return null;
        return switch (domainStatus) {
            case PLANNED -> co.improsvita.persistence.enums.SowingStatus.PLANNED;
            case IN_PROGRESS -> co.improsvita.persistence.enums.SowingStatus.IN_PROGRESS;
            case COMPLETED -> co.improsvita.persistence.enums.SowingStatus.COMPLETED;
            case FAILED -> co.improsvita.persistence.enums.SowingStatus.FAILED;
            case CANCELLED -> co.improsvita.persistence.enums.SowingStatus.CANCELLED;
        };
    }
}