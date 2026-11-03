package co.improsvita.persistence;

import co.improsvita.domain.model.SeedSupplier;
import co.improsvita.domain.repository.SeedSupplierRepository;
import co.improsvita.persistence.crud.ContactJpaRepository;
import co.improsvita.persistence.crud.SeedJpaRepository;
import co.improsvita.persistence.crud.SeedSupplierJpaRepository;
import co.improsvita.persistence.entities.ContactEntity;
import co.improsvita.persistence.entities.SeedEntity;
import co.improsvita.persistence.entities.SeedSupplierEntity;
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
    private final ContactJpaRepository contactJpaRepository;

    public SeedSupplierRepositoryImpl(SeedSupplierJpaRepository seedSupplierJpaRepository,
                                      SeedJpaRepository seedJpaRepository,
                                      ContactJpaRepository contactJpaRepository) {
        this.seedSupplierJpaRepository = seedSupplierJpaRepository;
        this.seedJpaRepository = seedJpaRepository;
        this.contactJpaRepository = contactJpaRepository;
    }

    @Override
    @Transactional
    public SeedSupplier save(SeedSupplier seedSupplier) {
        SeedEntity seed = seedJpaRepository.getReferenceById(seedSupplier.getSeedId());
        ContactEntity supplier = contactJpaRepository.getReferenceById(seedSupplier.getSupplierId());
        SeedSupplierEntity saved = seedSupplierJpaRepository.save(SeedSupplierMapper.toEntity(seedSupplier, seed, supplier));
        return SeedSupplierMapper.toDomain(saved);
    }

    @Override
    public Optional<SeedSupplier> findActiveBySeedAndSupplier(Integer seedId, Integer supplierId) {
        return seedSupplierJpaRepository.findBySeed_SeedIdAndSupplier_ContactIdAndActiveTrue(seedId, supplierId)
                .map(SeedSupplierMapper::toDomain);
    }

    @Override
    public boolean existsBySupplierId(Integer supplierId) {
        return seedSupplierJpaRepository.existsBySupplier_ContactId(supplierId);
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
        return seedSupplierJpaRepository.findBySupplier_ContactId(supplierId)
                .stream()
                .map(SeedSupplierMapper::toDomain)
                .collect(Collectors.toList());
    }
}