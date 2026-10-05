package co.improsvita.domain.repository;

import co.improsvita.domain.model.SeedLot;
import co.improsvita.domain.model.SeedLotStatus;

import java.util.List;

public interface SeedLotRepository {
    List<SeedLot> getAll();
    SeedLot getById(Integer id);
    SeedLot save(SeedLot lot);
    void deleteById(Integer id);
    List<SeedLot> getBySeedId(Integer seedId);
    List<SeedLot> getByLocationId(Integer locationId);
    List<SeedLot> getByStatus(SeedLotStatus status);
}