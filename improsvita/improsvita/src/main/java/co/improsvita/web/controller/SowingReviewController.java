package co.improsvita.web.controller;

import co.improsvita.domain.model.SowingReview;
import co.improsvita.domain.service.SowingReviewService;
import co.improsvita.domain.service.SowingService;
import co.improsvita.web.dto.SowingReviewRequest;
import co.improsvita.web.dto.SowingReviewResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sowings/{sowingId}/reviews")
public class SowingReviewController {

    private final SowingReviewService sowingReviewService;
    private final SowingService sowingService;

    public SowingReviewController(SowingReviewService sowingReviewService, SowingService sowingService) {
        this.sowingReviewService = sowingReviewService;
        this.sowingService = sowingService;
    }

    @GetMapping
    public ResponseEntity<List<SowingReviewResponse>> getReviews(@PathVariable Integer sowingId) {
        if (sowingService.getSowingById(sowingId) == null) {
            return ResponseEntity.notFound().build();
        }
        List<SowingReviewResponse> reviews = sowingReviewService.getReviews(sowingId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    public ResponseEntity<?> createReview(@PathVariable Integer sowingId,
                                          @RequestBody SowingReviewRequest request) {
        if (sowingService.getSowingById(sowingId) == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            Integer germinated = toInteger(request.getGerminatedQuantity());
            SowingReview created = sowingReviewService.createReview(
                    sowingId, request.getReviewDate(), germinated, request.getNotes());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    private Integer toInteger(BigDecimal value) {
        if (value == null) {
            return null;
        }
        try {
            return value.intValueExact();
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("La cantidad germinada debe ser un número entero");
        }
    }

    private SowingReviewResponse toResponse(SowingReview review) {
        SowingReviewResponse response = new SowingReviewResponse();
        response.setId(review.getId());
        response.setSowingId(review.getSowingId());
        response.setReviewDate(review.getReviewDate());
        response.setGerminatedQuantity(review.getGerminatedQuantity());
        response.setNotes(review.getNotes());
        return response;
    }
}