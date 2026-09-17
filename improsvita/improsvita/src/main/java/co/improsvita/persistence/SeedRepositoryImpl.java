package co.improsvita.persistence;

import co.improsvita.domain.model.Supplier;
import co.improsvita.domain.model.Seed;
import co.improsvita.domain.repository.SeedRepository;

import java.util.List;

public class SeedRepositoryImpl implements SeedRepository {

    @Override
    public Seed getById(Integer id) {
        return null;
    }

    @Override
    public Seed getByName(String name) {
        return null;
    }

    @Override
    public Seed save(Seed seed) {
        return null;
    }

    @Override
    public void deleteByName(String name) {
    }

    @Override
    public void deleteById(Integer id) {
    }

    @Override
    public List<Seed> getBySupplier(Supplier supplier) {
        return null;
    }

    @Override
    public List<Seed> getByType(Seed.SeedType type) {
        return null;
    }

    @Override
    public List<Seed> getByStockLess(Integer stock) {
        return null;
    }
}
