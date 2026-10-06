package co.improsvita.persistence;

import co.improsvita.domain.model.Seed;
import co.improsvita.domain.model.SeedType;
import co.improsvita.domain.repository.SeedRepository;
import co.improsvita.persistence.crud.SeedJpaRepository;
import co.improsvita.persistence.crud.SeedLotJpaRepository;
import co.improsvita.persistence.entities.SeedEntity;
import co.improsvita.persistence.mapper.SeedMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class SeedRepositoryImpl implements SeedRepository {

    private final SeedJpaRepository seedJpaRepository;
    private final SeedLotJpaRepository seedLotJpaRepository;

    public SeedRepositoryImpl(SeedJpaRepository seedJpaRepository, SeedLotJpaRepository seedLotJpaRepository) {
        this.seedJpaRepository = seedJpaRepository;
        this.seedLotJpaRepository = seedLotJpaRepository;
    }

    @Override
    public List<Seed> getAll() {
        return seedJpaRepository.findAll()
                .stream()
                .map(this::toDomainWithStock)
                .collect(Collectors.toList());
    }

    @Override
    public Seed getById(Integer id) {
        return seedJpaRepository.findById(id)
                .map(this::toDomainWithStock)
                .orElse(null);
    }

    @Override
    public Seed getByName(String name) {
        return seedJpaRepository.findByName(name)
                .map(this::toDomainWithStock)
                .orElse(null);
    }

    @Override
    @Transactional
    public Seed save(Seed seed) {
        SeedEntity entity = SeedMapper.toEntity(seed);
        SeedEntity saved = seedJpaRepository.save(entity);
        return toDomainWithStock(saved);
    }

    @Override
    @Transactional
    public void deleteByName(String name) {
        seedJpaRepository.deleteByName(name);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        seedJpaRepository.deleteById(id);
    }

    @Override
    public List<Seed> getBySupplierId(Integer supplierId) {
        return seedJpaRepository.findActiveBySupplierId(supplierId)
                .stream()
                .map(this::toDomainWithStock)
                .collect(Collectors.toList());
    }

    @Override
    public List<Seed> getByType(SeedType type) {
        co.improsvita.persistence.enums.SeedType entityType = switch (type) {
            case HYBRID -> co.improsvita.persistence.enums.SeedType.HYBRID;
            case TRADITIONAL -> co.improsvita.persistence.enums.SeedType.TRADITIONAL;
            case MODIFIED -> co.improsvita.persistence.enums.SeedType.MODIFIED;
        };
        return seedJpaRepository.findByType(entityType)
                .stream()
                .map(this::toDomainWithStock)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getStockBySeedId(Integer seedId) {
        return seedLotJpaRepository.sumAvailableBySeedId(seedId);
    }

    private Seed toDomainWithStock(SeedEntity entity) {
        Seed seed = SeedMapper.toDomain(entity);
        seed.setTotalAvailable(seedLotJpaRepository.sumAvailableBySeedId(entity.getSeedId()));
        return seed;
    }
}