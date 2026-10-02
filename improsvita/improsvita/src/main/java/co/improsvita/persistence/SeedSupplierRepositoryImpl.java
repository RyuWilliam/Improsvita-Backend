package co.improsvita.persistence;

import co.improsvita.domain.model.SeedSupplier;
import co.improsvita.domain.repository.SeedSupplierRepository;
import co.improsvita.persistence.crud.SeedJpaRepository;
import co.improsvita.persistence.crud.SeedSupplierJpaRepository;
import co.improsvita.persistence.crud.SupplierJpaRepository;
import co.improsvita.persistence.entities.SeedEntity;
import co.improsvita.persistence.entities.SeedSupplierEntity;
import co.improsvita.persistence.entities.SupplierEntity;
import co.improsvita.persistence.mapper.SeedSupplierMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class SeedSupplierRepositoryImpl implements SeedSupplierRepository {

    private final SeedSupplierJpaRepository seedSupplierJpaRepository;
    private final SeedJpaRepository seedJpaRepository;
    private final SupplierJpaRepository supplierJpaRepository;

    public SeedSupplierRepositoryImpl(SeedSupplierJpaRepository seedSupplierJpaRepository,
                                      SeedJpaRepository seedJpaRepository,
                                      SupplierJpaRepository supplierJpaRepository) {
        this.seedSupplierJpaRepository = seedSupplierJpaRepository;
        this.seedJpaRepository = seedJpaRepository;
        this.supplierJpaRepository = supplierJpaRepository;
    }

    @Override
    @Transactional
    public SeedSupplier save(SeedSupplier seedSupplier) {
        SeedEntity seed = seedJpaRepository.getReferenceById(seedSupplier.getSeedId());
        SupplierEntity supplier = supplierJpaRepository.getReferenceById(seedSupplier.getSupplierId());
        SeedSupplierEntity saved = seedSupplierJpaRepository.save(SeedSupplierMapper.toEntity(seedSupplier, seed, supplier));
        return SeedSupplierMapper.toDomain(saved);
    }

    @Override
    public Optional<SeedSupplier> findActiveBySeedAndSupplier(Integer seedId, Integer supplierId) {
        return seedSupplierJpaRepository.findBySeed_SeedIdAndSupplier_SupplierIdAndActiveTrue(seedId, supplierId)
                .map(SeedSupplierMapper::toDomain);
    }

    @Override
    public List<SeedSupplier> getBySeedId(Integer seedId) {
        return seedSupplierJpaRepository.findBySeed_SeedId(seedId)
                .stream()
                .map(SeedSupplierMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeedSupplier> getBySupplierId(Integer supplierId) {
        return seedSupplierJpaRepository.findBySupplier_SupplierId(supplierId)
                .stream()
                .map(SeedSupplierMapper::toDomain)
                .collect(Collectors.toList());
    }
}