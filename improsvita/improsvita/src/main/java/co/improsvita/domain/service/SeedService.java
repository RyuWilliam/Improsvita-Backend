package co.improsvita.domain.service;

import co.improsvita.domain.model.Seed;
import co.improsvita.domain.model.Supplier;
import co.improsvita.domain.repository.SeedRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeedService {

    private final SeedRepository seedRepository;

    public SeedService(SeedRepository seedRepository) {
        this.seedRepository = seedRepository;
    }

    public List<Seed> getAllSeeds() {
        return seedRepository.getAll();
    }

    public Seed getSeedById(Integer id) {
        return seedRepository.getById(id);
    }

    public Seed getSeedByName(String name) {
        return seedRepository.getByName(name);
    }

    public Seed createSeed(Seed seed) {
        return seedRepository.save(seed);
    }

    public Seed updateSeed(Seed seed) {
        return seedRepository.save(seed);
    }

    public void deleteSeedByName(String name) {
        seedRepository.deleteByName(name);
    }

    public void deleteSeedById(Integer id) {
        seedRepository.deleteById(id);
    }

    public List<Seed> getSeedsBySupplier(Supplier supplier) {
        return seedRepository.getBySupplier(supplier);
    }

    public List<Seed> getSeedsByType(Seed.SeedType type) {
        return seedRepository.getByType(type);
    }

    public List<Seed> getSeedsByStockLess(Integer stock) {
        return seedRepository.getByStockLess(stock);
    }
}
