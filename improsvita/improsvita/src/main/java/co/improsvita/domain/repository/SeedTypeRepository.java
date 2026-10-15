package co.improsvita.domain.repository;

import co.improsvita.domain.model.SeedTypeCatalog;

import java.util.List;

public interface SeedTypeRepository {
    List<SeedTypeCatalog> getAll();
    SeedTypeCatalog getById(Integer id);
    SeedTypeCatalog getByName(String name);
    SeedTypeCatalog save(SeedTypeCatalog seedType);
    void deleteById(Integer id);
    boolean isInUse(Integer id);
}