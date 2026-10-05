package co.improsvita.persistence;

import co.improsvita.domain.model.SeedMovement;
import co.improsvita.domain.repository.SeedMovementRepository;
import co.improsvita.persistence.crud.SeedLotJpaRepository;
import co.improsvita.persistence.crud.SeedMovementJpaRepository;
import co.improsvita.persistence.crud.SupplierJpaRepository;
import co.improsvita.persistence.entities.SeedLotEntity;
import co.improsvita.persistence.entities.SeedMovementEntity;
import co.improsvita.persistence.entities.SupplierEntity;
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
    private final SupplierJpaRepository supplierJpaRepository;

    public SeedMovementRepositoryImpl(SeedMovementJpaRepository seedMovementJpaRepository,
                                      SeedLotJpaRepository seedLotJpaRepository,
                                      SupplierJpaRepository supplierJpaRepository) {
        this.seedMovementJpaRepository = seedMovementJpaRepository;
        this.seedLotJpaRepository = seedLotJpaRepository;
        this.supplierJpaRepository = supplierJpaRepository;
    }

    @Override
    @Transactional
    public SeedMovement save(SeedMovement movement) {
        SeedLotEntity lot = seedLotJpaRepository.getReferenceById(movement.getLotId());
        SupplierEntity supplier = movement.getSupplierId() != null
                ? supplierJpaRepository.getReferenceById(movement.getSupplierId())
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
        return seedMovementJpaRepository.findBySupplier_SupplierId(supplierId)
                .stream()
                .map(SeedMovementMapper::toDomain)
                .collect(Collectors.toList());
    }
}