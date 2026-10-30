package co.improsvita.domain.repository;

import co.improsvita.domain.model.SeedMovement;

import java.util.List;

public interface SeedMovementRepository {
    SeedMovement save(SeedMovement movement);
    List<SeedMovement> getByLotId(Integer lotId);
    List<SeedMovement> getBySupplierId(Integer supplierId);
    List<SeedMovement> getBySowingId(Integer sowingId);
    void detachFromSowing(Integer sowingId);
}