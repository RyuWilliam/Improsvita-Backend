package co.improsvita.persistence.mapper;

import co.improsvita.domain.model.SeedMovement;
import co.improsvita.persistence.entities.SeedLotEntity;
import co.improsvita.persistence.entities.SeedMovementEntity;
import co.improsvita.persistence.entities.SupplierEntity;

public class SeedMovementMapper {

    private SeedMovementMapper() {
    }

    public static SeedMovement toDomain(SeedMovementEntity entity) {
        if (entity == null) return null;
        SeedMovement movement = new SeedMovement();
        movement.setId(entity.getTransactionId());
        movement.setLotId(entity.getSeedLot() != null ? entity.getSeedLot().getLotId() : null);
        movement.setSupplierId(entity.getSupplier() != null ? entity.getSupplier().getSupplierId() : null);
        movement.setMovementType(mapType(entity.getMovementType()));
        movement.setQuantity(entity.getQuantity());
        movement.setMovementDate(entity.getMovementDate());
        movement.setReason(entity.getReason());
        return movement;
    }

    public static SeedMovementEntity toEntity(SeedMovement domain, SeedLotEntity lot, SupplierEntity supplier) {
        if (domain == null) return null;
        SeedMovementEntity entity = new SeedMovementEntity();
        entity.setTransactionId(domain.getId());
        entity.setSeedLot(lot);
        entity.setSupplier(supplier);
        entity.setMovementType(mapType(domain.getMovementType()));
        entity.setQuantity(domain.getQuantity());
        entity.setMovementDate(domain.getMovementDate());
        entity.setReason(domain.getReason());
        return entity;
    }

    private static co.improsvita.domain.model.MovementType mapType(co.improsvita.persistence.enums.MovementType entityType) {
        if (entityType == null) return null;
        return switch (entityType) {
            case ENTRY -> co.improsvita.domain.model.MovementType.ENTRY;
            case EXIT -> co.improsvita.domain.model.MovementType.EXIT;
            case ADJUSTMENT -> co.improsvita.domain.model.MovementType.ADJUSTMENT;
        };
    }

    private static co.improsvita.persistence.enums.MovementType mapType(co.improsvita.domain.model.MovementType domainType) {
        if (domainType == null) return null;
        return switch (domainType) {
            case ENTRY -> co.improsvita.persistence.enums.MovementType.ENTRY;
            case EXIT -> co.improsvita.persistence.enums.MovementType.EXIT;
            case ADJUSTMENT -> co.improsvita.persistence.enums.MovementType.ADJUSTMENT;
        };
    }
}