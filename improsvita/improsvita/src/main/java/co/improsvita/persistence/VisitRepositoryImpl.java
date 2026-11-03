package co.improsvita.persistence;

import co.improsvita.domain.model.Visit;
import co.improsvita.domain.repository.VisitRepository;
import co.improsvita.persistence.crud.SowingJpaRepository;
import co.improsvita.persistence.crud.VisitJpaRepository;
import co.improsvita.persistence.entities.SowingEntity;
import co.improsvita.persistence.mapper.VisitMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class VisitRepositoryImpl implements VisitRepository {

    private final VisitJpaRepository visitJpaRepository;
    private final SowingJpaRepository sowingJpaRepository;

    public VisitRepositoryImpl(VisitJpaRepository visitJpaRepository,
                               SowingJpaRepository sowingJpaRepository) {
        this.visitJpaRepository = visitJpaRepository;
        this.sowingJpaRepository = sowingJpaRepository;
    }

    @Override
    public List<Visit> getBySowingId(Integer sowingId) {
        return visitJpaRepository.findBySowing_SowingIdOrderByVisitDateDesc(sowingId)
                .stream()
                .map(VisitMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Visit getById(Integer id) {
        return visitJpaRepository.findById(id)
                .map(VisitMapper::toDomain)
                .orElse(null);
    }

    @Override
    @Transactional
    public Visit save(Visit visit) {
        SowingEntity sowing = sowingJpaRepository.getReferenceById(visit.getSowingId());
        return VisitMapper.toDomain(visitJpaRepository.save(VisitMapper.toEntity(visit, sowing)));
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        visitJpaRepository.deleteById(id);
    }
}
