package co.improsvita.domain.repository;

import co.improsvita.domain.model.Seed;
import co.improsvita.domain.model.SeedType;

import java.math.BigDecimal;
import java.util.List;

public interface SeedRepository {
    List<Seed> getAll();
    Seed getById(Integer id);
    Seed getByName(String name);
    Seed save(Seed seed);
    void deleteByName(String name);
    void deleteById(Integer id);
    List<Seed> getBySupplierId(Integer supplierId);
    List<Seed> getByType(SeedType type);
    BigDecimal getStockBySeedId(Integer seedId);
}