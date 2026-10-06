package co.improsvita.domain.repository;

import co.improsvita.domain.model.Location;

import java.util.List;

public interface LocationRepository {
    List<Location> getAll();
    Location getById(Integer id);
    Location save(Location location);
    void deleteById(Integer id);
}