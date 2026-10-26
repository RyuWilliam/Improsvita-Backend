package co.improsvita.persistence.mapper;

import co.improsvita.domain.model.SowingReview;
import co.improsvita.persistence.entities.SowingEntity;
import co.improsvita.persistence.entities.SowingReviewEntity;

public class SowingReviewMapper {

    private SowingReviewMapper() {
    }

    public static SowingReview toDomain(SowingReviewEntity entity) {
        if (entity == null) return null;
        SowingReview review = new SowingReview();
        review.setId(entity.getReviewId());
        review.setSowingId(entity.getSowing() != null ? entity.getSowing().getSowingId() : null);
        review.setReviewDate(entity.getReviewDate());
        review.setGerminatedQuantity(entity.getGerminatedQuantity());
        review.setNotes(entity.getNotes());
        return review;
    }

    public static SowingReviewEntity toEntity(SowingReview domain, SowingEntity sowing) {
        if (domain == null) return null;
        SowingReviewEntity entity = new SowingReviewEntity();
        entity.setReviewId(domain.getId());
        entity.setSowing(sowing);
        entity.setReviewDate(domain.getReviewDate());
        entity.setGerminatedQuantity(domain.getGerminatedQuantity());
        entity.setNotes(domain.getNotes());
        return entity;
    }
}