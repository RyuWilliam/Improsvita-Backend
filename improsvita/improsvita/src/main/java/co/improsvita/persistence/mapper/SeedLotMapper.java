package co.improsvita.persistence.mapper;

import co.improsvita.domain.model.SeedLot;
import co.improsvita.persistence.entities.LocationEntity;
import co.improsvita.persistence.entities.SeedEntity;
import co.improsvita.persistence.entities.SeedLotEntity;

public class SeedLotMapper {

    private SeedLotMapper() {
    }

    public static SeedLot toDomain(SeedLotEntity entity) {
        if (entity == null) return null;
        SeedLot lot = new SeedLot();
        lot.setId(entity.getLotId());
        lot.setLotNumber(entity.getLotNumber());
        lot.setSeedId(entity.getSeed() != null ? entity.getSeed().getSeedId() : null);
        lot.setLocationId(entity.getLocation() != null ? entity.getLocation().getLocationId() : null);
        lot.setEntryDate(entity.getEntryDate());
        lot.setDueDate(entity.getDueDate());
        lot.setInitialQuantity(entity.getInitialQuantity());
        lot.setAvailableQuantity(entity.getAvailableQuantity());
        lot.setStatus(mapStatus(entity.getStatus()));
        return lot;
    }

    public static SeedLotEntity toEntity(SeedLot domain, SeedEntity seed, LocationEntity location) {
        if (domain == null) return null;
        SeedLotEntity entity = new SeedLotEntity();
        entity.setLotId(domain.getId());
        entity.setLotNumber(domain.getLotNumber());
        entity.setSeed(seed);
        entity.setLocation(location);
        entity.setEntryDate(domain.getEntryDate());
        entity.setDueDate(domain.getDueDate());
        entity.setInitialQuantity(domain.getInitialQuantity());
        entity.setAvailableQuantity(domain.getAvailableQuantity());
        entity.setStatus(mapStatus(domain.getStatus()));
        return entity;
    }

    private static co.improsvita.domain.model.SeedLotStatus mapStatus(co.improsvita.persistence.enums.SeedLotStatus entityStatus) {
        if (entityStatus == null) return null;
        return switch (entityStatus) {
            case AVAILABLE -> co.improsvita.domain.model.SeedLotStatus.AVAILABLE;
            case DEPLETED -> co.improsvita.domain.model.SeedLotStatus.DEPLETED;
            case EXPIRED -> co.improsvita.domain.model.SeedLotStatus.EXPIRED;
            case DISCARDED -> co.improsvita.domain.model.SeedLotStatus.DISCARDED;
        };
    }

    private static co.improsvita.persistence.enums.SeedLotStatus mapStatus(co.improsvita.domain.model.SeedLotStatus domainStatus) {
        if (domainStatus == null) return null;
        return switch (domainStatus) {
            case AVAILABLE -> co.improsvita.persistence.enums.SeedLotStatus.AVAILABLE;
            case DEPLETED -> co.improsvita.persistence.enums.SeedLotStatus.DEPLETED;
            case EXPIRED -> co.improsvita.persistence.enums.SeedLotStatus.EXPIRED;
            case DISCARDED -> co.improsvita.persistence.enums.SeedLotStatus.DISCARDED;
        };
    }
}