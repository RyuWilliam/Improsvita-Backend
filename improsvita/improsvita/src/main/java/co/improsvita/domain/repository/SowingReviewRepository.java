package co.improsvita.domain.repository;

import co.improsvita.domain.model.SowingReview;

import java.util.List;

public interface SowingReviewRepository {
    SowingReview save(SowingReview review);
    List<SowingReview> getBySowingId(Integer sowingId);
    SowingReview getLatestBySowingId(Integer sowingId);
}