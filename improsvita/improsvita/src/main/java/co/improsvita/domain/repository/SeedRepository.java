package co.improsvita.domain.repository;

import co.improsvita.domain.model.Seed;

import java.util.List;

public interface SeedRepository {
    List<Seed> getAll();
    Seed getById(Integer id);
    Seed getByName(String name);
    Seed save(Seed seed);
    void deleteById(Integer id);
    List<Seed> getBySupplierId(Integer supplierId);
    List<Seed> getByTypeName(String typeName);
}