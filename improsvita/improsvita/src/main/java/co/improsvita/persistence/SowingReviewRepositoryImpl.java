package co.improsvita.persistence;

import co.improsvita.domain.model.SowingReview;
import co.improsvita.domain.repository.SowingReviewRepository;
import co.improsvita.persistence.crud.SowingJpaRepository;
import co.improsvita.persistence.crud.SowingReviewJpaRepository;
import co.improsvita.persistence.entities.SowingEntity;
import co.improsvita.persistence.entities.SowingReviewEntity;
import co.improsvita.persistence.mapper.SowingReviewMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class SowingReviewRepositoryImpl implements SowingReviewRepository {

    private final SowingReviewJpaRepository sowingReviewJpaRepository;
    private final SowingJpaRepository sowingJpaRepository;

    public SowingReviewRepositoryImpl(SowingReviewJpaRepository sowingReviewJpaRepository,
                                      SowingJpaRepository sowingJpaRepository) {
        this.sowingReviewJpaRepository = sowingReviewJpaRepository;
        this.sowingJpaRepository = sowingJpaRepository;
    }

    @Override
    @Transactional
    public SowingReview save(SowingReview review) {
        SowingEntity sowing = sowingJpaRepository.getReferenceById(review.getSowingId());
        SowingReviewEntity saved = sowingReviewJpaRepository.save(SowingReviewMapper.toEntity(review, sowing));
        return SowingReviewMapper.toDomain(saved);
    }

    @Override
    public List<SowingReview> getBySowingId(Integer sowingId) {
        return sowingReviewJpaRepository.findBySowing_SowingIdOrderByReviewDateDescReviewIdDesc(sowingId)
                .stream()
                .map(SowingReviewMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public SowingReview getLatestBySowingId(Integer sowingId) {
        return sowingReviewJpaRepository.findFirstBySowing_SowingIdOrderByReviewDateDescReviewIdDesc(sowingId)
                .map(SowingReviewMapper::toDomain)
                .orElse(null);
    }
}