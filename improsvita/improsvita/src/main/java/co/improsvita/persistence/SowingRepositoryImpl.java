package co.improsvita.persistence;

import co.improsvita.domain.model.Sowing;
import co.improsvita.domain.model.SowingStatus;
import co.improsvita.domain.repository.SowingRepository;
import co.improsvita.persistence.crud.BedJpaRepository;
import co.improsvita.persistence.crud.SeedLotJpaRepository;
import co.improsvita.persistence.crud.SowingJpaRepository;
import co.improsvita.persistence.entities.BedEntity;
import co.improsvita.persistence.entities.SeedLotEntity;
import co.improsvita.persistence.entities.SowingEntity;
import co.improsvita.persistence.mapper.SowingMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class SowingRepositoryImpl implements SowingRepository {

    private final SowingJpaRepository sowingJpaRepository;
    private final SeedLotJpaRepository seedLotJpaRepository;
    private final BedJpaRepository bedJpaRepository;

    public SowingRepositoryImpl(SowingJpaRepository sowingJpaRepository,
                                SeedLotJpaRepository seedLotJpaRepository,
                                BedJpaRepository bedJpaRepository) {
        this.sowingJpaRepository = sowingJpaRepository;
        this.seedLotJpaRepository = seedLotJpaRepository;
        this.bedJpaRepository = bedJpaRepository;
    }

    @Override
    public List<Sowing> getAll() {
        return sowingJpaRepository.findAll()
                .stream()
                .map(SowingMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Sowing getById(Integer id) {
        return sowingJpaRepository.findById(id)
                .map(SowingMapper::toDomain)
                .orElse(null);
    }

    @Override
    @Transactional
    public Sowing save(Sowing sowing) {
        SeedLotEntity lot = seedLotJpaRepository.getReferenceById(sowing.getLotId());
        BedEntity bed = bedJpaRepository.getReferenceById(sowing.getBedId());
        SowingEntity saved = sowingJpaRepository.save(SowingMapper.toEntity(sowing, lot, bed));
        return SowingMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        sowingJpaRepository.deleteById(id);
    }

    @Override
    public List<Sowing> getByLotId(Integer lotId) {
        return sowingJpaRepository.findBySeedLot_LotId(lotId)
                .stream()
                .map(SowingMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Sowing> getByBedId(Integer bedId) {
        return sowingJpaRepository.findByBed_BedId(bedId)
                .stream()
                .map(SowingMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Sowing> getByStatus(SowingStatus status) {
        co.improsvita.persistence.enums.SowingStatus entityStatus = switch (status) {
            case PLANNED -> co.improsvita.persistence.enums.SowingStatus.PLANNED;
            case IN_PROGRESS -> co.improsvita.persistence.enums.SowingStatus.IN_PROGRESS;
            case COMPLETED -> co.improsvita.persistence.enums.SowingStatus.COMPLETED;
            case FAILED -> co.improsvita.persistence.enums.SowingStatus.FAILED;
            case CANCELLED -> co.improsvita.persistence.enums.SowingStatus.CANCELLED;
        };
        return sowingJpaRepository.findByStatus(entityStatus)
                .stream()
                .map(SowingMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Sowing> getByBedCode(String code) {
        return sowingJpaRepository.findByBed_Code(code)
                .stream()
                .map(SowingMapper::toDomain)
                .collect(Collectors.toList());
    }
}