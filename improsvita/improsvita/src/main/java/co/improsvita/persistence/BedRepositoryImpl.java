package co.improsvita.persistence;

import co.improsvita.domain.model.Bed;
import co.improsvita.domain.repository.BedRepository;
import co.improsvita.persistence.crud.BedJpaRepository;
import co.improsvita.persistence.entities.BedEntity;
import co.improsvita.persistence.mapper.BedMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class BedRepositoryImpl implements BedRepository {

    private final BedJpaRepository bedJpaRepository;

    public BedRepositoryImpl(BedJpaRepository bedJpaRepository) {
        this.bedJpaRepository = bedJpaRepository;
    }

    @Override
    public List<Bed> getAll() {
        return bedJpaRepository.findAll()
                .stream()
                .map(BedMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Bed getById(Integer id) {
        return bedJpaRepository.findById(id)
                .map(BedMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Bed getByCode(String code) {
        return bedJpaRepository.findByCode(code)
                .map(BedMapper::toDomain)
                .orElse(null);
    }

    @Override
    @Transactional
    public Bed save(Bed bed) {
        BedEntity saved = bedJpaRepository.save(BedMapper.toEntity(bed));
        return BedMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        bedJpaRepository.deleteById(id);
    }
}