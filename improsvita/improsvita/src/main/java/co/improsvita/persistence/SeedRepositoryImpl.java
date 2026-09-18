package co.improsvita.persistence;

import co.improsvita.domain.model.Seed;
import co.improsvita.domain.model.Supplier;
import co.improsvita.domain.repository.SeedRepository;
import co.improsvita.persistence.crud.SeedJpaRepository;
import co.improsvita.persistence.entities.SeedEntity;
import co.improsvita.persistence.enums.SeedType;
import co.improsvita.persistence.mapper.SeedMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SeedRepositoryImpl implements SeedRepository {

    private final SeedJpaRepository seedJpaRepository;

    public SeedRepositoryImpl(SeedJpaRepository seedJpaRepository) {
        this.seedJpaRepository = seedJpaRepository;
    }

    @Override
    public List<Seed> getAll() {
        return seedJpaRepository.findAll()
                .stream()
                .map(SeedMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Seed getById(Integer id) {
        return seedJpaRepository.findById(id)
                .map(SeedMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Seed getByName(String name) {
        return seedJpaRepository.findByName(name)
                .map(SeedMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Seed save(Seed seed) {
        SeedEntity entity = SeedMapper.toEntity(seed);
        SeedEntity saved = seedJpaRepository.save(entity);
        return SeedMapper.toDomain(saved);
    }

    @Override
    public void deleteByName(String name) {
        seedJpaRepository.deleteByName(name);
    }

    @Override
    public void deleteById(Integer id) {
        seedJpaRepository.deleteById(id);
    }

    @Override
    public List<Seed> getBySupplier(Supplier supplier) {
        SeedEntity supplierEntity = new SeedEntity();
        supplierEntity.setSupplier(co.improsvita.persistence.mapper.ProviderMapper.toEntity(supplier));
        return seedJpaRepository.findBySupplier(supplierEntity.getSupplier())
                .stream()
                .map(SeedMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Seed> getByType(Seed.SeedType type) {
        SeedType entityType = switch (type) {
            case HYBRID -> SeedType.HYBRID;
            case TRADITIONAL -> SeedType.TRADITIONAL;
            case MODIFIED -> SeedType.MODIFIED;
        };
        return seedJpaRepository.findByType(entityType)
                .stream()
                .map(SeedMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Seed> getByStockLess(Integer stock) {
        return seedJpaRepository.findByQuantityLessThan(stock)
                .stream()
                .map(SeedMapper::toDomain)
                .collect(Collectors.toList());
    }
}
