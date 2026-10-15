package co.improsvita.persistence;

import co.improsvita.domain.model.SeedTypeCatalog;
import co.improsvita.domain.repository.SeedTypeRepository;
import co.improsvita.persistence.crud.SeedJpaRepository;
import co.improsvita.persistence.crud.SeedTypeJpaRepository;
import co.improsvita.persistence.enums.SeedType;
import co.improsvita.persistence.mapper.SeedTypeMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SeedTypeRepositoryImpl implements SeedTypeRepository {

    private final SeedTypeJpaRepository seedTypeJpaRepository;
    private final SeedJpaRepository seedJpaRepository;

    public SeedTypeRepositoryImpl(SeedTypeJpaRepository seedTypeJpaRepository,
                                  SeedJpaRepository seedJpaRepository) {
        this.seedTypeJpaRepository = seedTypeJpaRepository;
        this.seedJpaRepository = seedJpaRepository;
    }

    @Override
    public List<SeedTypeCatalog> getAll() {
        return seedTypeJpaRepository.findAll()
                .stream()
                .map(SeedTypeMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public SeedTypeCatalog getById(Integer id) {
        return seedTypeJpaRepository.findById(id)
                .map(SeedTypeMapper::toDomain)
                .orElse(null);
    }

    @Override
    public SeedTypeCatalog getByName(String name) {
        return seedTypeJpaRepository.findByNameIgnoreCase(name)
                .map(SeedTypeMapper::toDomain)
                .orElse(null);
    }

    @Override
    public SeedTypeCatalog save(SeedTypeCatalog seedType) {
        var saved = seedTypeJpaRepository.save(SeedTypeMapper.toEntity(seedType));
        return SeedTypeMapper.toDomain(saved);
    }

    @Override
    public void deleteById(Integer id) {
        seedTypeJpaRepository.deleteById(id);
    }

    @Override
    public boolean isInUse(Integer id) {
        SeedTypeCatalog seedType = getById(id);
        if (seedType == null) {
            return false;
        }
        try {
            return seedJpaRepository.existsByType(SeedType.valueOf(seedType.getName()));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}