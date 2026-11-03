package co.improsvita.persistence;

import co.improsvita.domain.model.SeedMovement;
import co.improsvita.domain.repository.SeedMovementRepository;
import co.improsvita.persistence.crud.ContactJpaRepository;
import co.improsvita.persistence.crud.SeedLotJpaRepository;
import co.improsvita.persistence.crud.SeedMovementJpaRepository;
import co.improsvita.persistence.entities.ContactEntity;
import co.improsvita.persistence.entities.SeedLotEntity;
import co.improsvita.persistence.entities.SeedMovementEntity;
import co.improsvita.persistence.mapper.SeedMovementMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class SeedMovementRepositoryImpl implements SeedMovementRepository {

    private final SeedMovementJpaRepository seedMovementJpaRepository;
    private final SeedLotJpaRepository seedLotJpaRepository;
    private final ContactJpaRepository contactJpaRepository;

    public SeedMovementRepositoryImpl(SeedMovementJpaRepository seedMovementJpaRepository,
                                      SeedLotJpaRepository seedLotJpaRepository,
                                      ContactJpaRepository contactJpaRepository) {
        this.seedMovementJpaRepository = seedMovementJpaRepository;
        this.seedLotJpaRepository = seedLotJpaRepository;
        this.contactJpaRepository = contactJpaRepository;
    }

    @Override
    @Transactional
    public SeedMovement save(SeedMovement movement) {
        SeedLotEntity lot = seedLotJpaRepository.getReferenceById(movement.getLotId());
        ContactEntity supplier = movement.getSupplierId() != null
                ? contactJpaRepository.getReferenceById(movement.getSupplierId())
                : null;
        SeedMovementEntity saved = seedMovementJpaRepository.save(SeedMovementMapper.toEntity(movement, lot, supplier));
        return SeedMovementMapper.toDomain(saved);
    }

    @Override
    public List<SeedMovement> getByLotId(Integer lotId) {
        return seedMovementJpaRepository.findBySeedLot_LotIdOrderByMovementDateAsc(lotId)
                .stream()
                .map(SeedMovementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeedMovement> getBySupplierId(Integer supplierId) {
        return seedMovementJpaRepository.findBySupplier_ContactId(supplierId)
                .stream()
                .map(SeedMovementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsBySupplierId(Integer supplierId) {
        return seedMovementJpaRepository.existsBySupplier_ContactId(supplierId);
    }
}