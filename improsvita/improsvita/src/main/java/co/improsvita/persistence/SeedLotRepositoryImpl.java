package co.improsvita.persistence;

import co.improsvita.domain.model.SeedLot;
import co.improsvita.domain.model.SeedLotStatus;
import co.improsvita.domain.repository.SeedLotRepository;
import co.improsvita.persistence.crud.LocationJpaRepository;
import co.improsvita.persistence.crud.SeedJpaRepository;
import co.improsvita.persistence.crud.SeedLotJpaRepository;
import co.improsvita.persistence.entities.LocationEntity;
import co.improsvita.persistence.entities.SeedEntity;
import co.improsvita.persistence.entities.SeedLotEntity;
import co.improsvita.persistence.mapper.SeedLotMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class SeedLotRepositoryImpl implements SeedLotRepository {

    private final SeedLotJpaRepository seedLotJpaRepository;
    private final SeedJpaRepository seedJpaRepository;
    private final LocationJpaRepository locationJpaRepository;

    public SeedLotRepositoryImpl(SeedLotJpaRepository seedLotJpaRepository,
                                 SeedJpaRepository seedJpaRepository,
                                 LocationJpaRepository locationJpaRepository) {
        this.seedLotJpaRepository = seedLotJpaRepository;
        this.seedJpaRepository = seedJpaRepository;
        this.locationJpaRepository = locationJpaRepository;
    }

    @Override
    public List<SeedLot> getAll() {
        return seedLotJpaRepository.findAll()
                .stream()
                .map(SeedLotMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public SeedLot getById(Integer id) {
        return seedLotJpaRepository.findById(id)
                .map(SeedLotMapper::toDomain)
                .orElse(null);
    }

    @Override
    @Transactional
    public SeedLot save(SeedLot lot) {
        SeedEntity seed = seedJpaRepository.getReferenceById(lot.getSeedId());
        LocationEntity location = locationJpaRepository.getReferenceById(lot.getLocationId());
        SeedLotEntity saved = seedLotJpaRepository.save(SeedLotMapper.toEntity(lot, seed, location));
        return SeedLotMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        seedLotJpaRepository.deleteById(id);
    }

    @Override
    public List<SeedLot> getBySeedId(Integer seedId) {
        return seedLotJpaRepository.findBySeed_SeedId(seedId)
                .stream()
                .map(SeedLotMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeedLot> getByLocationId(Integer locationId) {
        return seedLotJpaRepository.findByLocation_LocationId(locationId)
                .stream()
                .map(SeedLotMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeedLot> getByStatus(SeedLotStatus status) {
        co.improsvita.persistence.enums.SeedLotStatus entityStatus = switch (status) {
            case AVAILABLE -> co.improsvita.persistence.enums.SeedLotStatus.AVAILABLE;
            case DEPLETED -> co.improsvita.persistence.enums.SeedLotStatus.DEPLETED;
            case EXPIRED -> co.improsvita.persistence.enums.SeedLotStatus.EXPIRED;
            case DISCARDED -> co.improsvita.persistence.enums.SeedLotStatus.DISCARDED;
        };
        return seedLotJpaRepository.findByStatus(entityStatus)
                .stream()
                .map(SeedLotMapper::toDomain)
                .collect(Collectors.toList());
    }
}