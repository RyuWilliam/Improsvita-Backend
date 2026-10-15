package co.improsvita.domain.service;

import co.improsvita.domain.model.Sowing;
import co.improsvita.domain.model.SowingReview;
import co.improsvita.domain.model.SowingStatus;
import co.improsvita.domain.repository.SowingRepository;
import co.improsvita.domain.repository.SowingReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class SowingReviewService {

    private static final Set<SowingStatus> TERMINAL = Set.of(
            SowingStatus.COMPLETED, SowingStatus.FAILED, SowingStatus.CANCELLED);
    private static final int MAX_NOTES_LENGTH = 1000;

    private final SowingRepository sowingRepository;
    private final SowingReviewRepository sowingReviewRepository;

    public SowingReviewService(SowingRepository sowingRepository,
                               SowingReviewRepository sowingReviewRepository) {
        this.sowingRepository = sowingRepository;
        this.sowingReviewRepository = sowingReviewRepository;
    }

    public List<SowingReview> getReviews(Integer sowingId) {
        return sowingReviewRepository.getBySowingId(sowingId);
    }

    /** Cantidad germinada de la última revisión, o null si la siembra no tiene revisiones. */
    public Integer getLatestGerminatedQuantity(Integer sowingId) {
        SowingReview latest = sowingReviewRepository.getLatestBySowingId(sowingId);
        return latest != null ? latest.getGerminatedQuantity() : null;
    }

    public SowingReview createReview(Integer sowingId, LocalDate reviewDate,
                                     Integer germinatedQuantity, String notes) {
        Sowing sowing = sowingRepository.getById(sowingId);
        if (sowing == null) {
            throw new IllegalArgumentException("Siembra no encontrada: " + sowingId);
        }
        if (Boolean.FALSE.equals(sowing.getActive()) || TERMINAL.contains(sowing.getStatus())) {
            throw new IllegalStateException("La siembra " + sowingId
                    + " no admite revisiones: está inactiva o en estado terminal");
        }

        if (germinatedQuantity == null) {
            throw new IllegalArgumentException("La cantidad germinada es obligatoria");
        }
        if (germinatedQuantity < 0) {
            throw new IllegalArgumentException("La cantidad germinada no puede ser negativa");
        }
        if (germinatedQuantity > sowing.getQuantitySown()) {
            throw new IllegalArgumentException("La cantidad germinada (" + germinatedQuantity
                    + ") no puede superar la cantidad sembrada (" + sowing.getQuantitySown() + ")");
        }

        LocalDate date = reviewDate != null ? reviewDate : LocalDate.now();
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de revisión no puede ser futura");
        }
        if (sowing.getSowingDate() != null && date.isBefore(sowing.getSowingDate())) {
            throw new IllegalArgumentException("La fecha de revisión no puede ser anterior a la fecha de siembra");
        }

        SowingReview review = new SowingReview();
        review.setSowingId(sowingId);
        review.setReviewDate(date);
        review.setGerminatedQuantity(germinatedQuantity);
        review.setNotes(cleanNotes(notes));
        return sowingReviewRepository.save(review);
    }

    private String cleanNotes(String notes) {
        if (notes == null || notes.isBlank()) {
            return null;
        }
        String clean = notes.trim();
        if (clean.length() > MAX_NOTES_LENGTH) {
            throw new IllegalArgumentException(
                    "Las observaciones no pueden superar los " + MAX_NOTES_LENGTH + " caracteres");
        }
        return clean;
    }
}