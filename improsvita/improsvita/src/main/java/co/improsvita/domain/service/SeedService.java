package co.improsvita.domain.service;

import co.improsvita.domain.model.Seed;
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

    public List<Seed> getSeedsBySupplierId(Integer supplierId) {
        return seedRepository.getBySupplierId(supplierId);
    }

        public List<Seed> getSeedsByType(String typeName) {
        return seedRepository.getByTypeName(typeName);
    }

    public List<Seed> getSeedsByStockLess(Long stock) {
        return seedRepository.getAll()
                .stream()
                .filter(seed -> seed.getTotalAvailable() != null && seed.getTotalAvailable() < stock)
                .toList();
    }
}