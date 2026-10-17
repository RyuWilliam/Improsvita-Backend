package co.improsvita.web.controller;

import co.improsvita.domain.model.Visit;
import co.improsvita.domain.model.VisitMethod;
import co.improsvita.domain.model.VisitResult;
import co.improsvita.domain.service.SowingService;
import co.improsvita.domain.service.VisitService;
import co.improsvita.web.dto.VisitRequest;
import co.improsvita.web.dto.VisitResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sowings/{sowingId}/visits")
public class VisitController {

    private final VisitService visitService;
    private final SowingService sowingService;

    public VisitController(VisitService visitService, SowingService sowingService) {
        this.visitService = visitService;
        this.sowingService = sowingService;
    }

    @GetMapping
    public ResponseEntity<?> getVisits(@PathVariable Integer sowingId) {
        if (sowingService.getSowingById(sowingId) == null) {
            return ResponseEntity.notFound().build();
        }
        List<VisitResponse> visits = visitService.getVisitsBySowing(sowingId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(visits);
    }

    @PostMapping
    public ResponseEntity<?> createVisit(@PathVariable Integer sowingId, @RequestBody VisitRequest request) {
        if (sowingService.getSowingById(sowingId) == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            Visit created = visitService.createVisit(
                    sowingId, request.getVisitDate(), request.getSuitableEstimated(),
                    parseMethod(request.getVisitMethod()), parseResult(request.getResult()));
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{visitId}")
    public ResponseEntity<?> updateVisit(@PathVariable Integer sowingId, @PathVariable Integer visitId,
                                         @RequestBody VisitRequest request) {
        try {
            Visit visit = visitService.updateVisit(
                    visitId, request.getVisitDate(), request.getSuitableEstimated(),
                    parseMethod(request.getVisitMethod()), parseResult(request.getResult()),
                    request.getActive());
            return ResponseEntity.ok(toResponse(visit));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{visitId}")
    public ResponseEntity<?> deleteVisit(@PathVariable Integer sowingId, @PathVariable Integer visitId) {
        try {
            visitService.deleteVisitById(visitId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private VisitMethod parseMethod(String method) {
        if (method == null || method.isBlank()) {
            return null;
        }
        try {
            return VisitMethod.valueOf(method.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Método de visita inválido: " + method
                    + ". Permitidos: INSPECCION_VISUAL, MUESTREO, CONTEO_TOTAL");
        }
    }

    private VisitResult parseResult(String result) {
        if (result == null || result.isBlank()) {
            return null;
        }
        try {
            return VisitResult.valueOf(result.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Resultado inválido: " + result
                    + ". Permitidos: ESTIMACION, CONTEO_FINAL, VALIDADA");
        }
    }

    private VisitResponse toResponse(Visit visit) {
        VisitResponse response = new VisitResponse();
        response.setId(visit.getId());
        response.setSowingId(visit.getSowingId());
        response.setVisitDate(visit.getVisitDate());
        response.setSuitableEstimated(visit.getSuitableEstimated());
        response.setVisitMethod(visit.getVisitMethod() != null ? visit.getVisitMethod().name() : null);
        response.setResult(visit.getResult() != null ? visit.getResult().name() : null);
        response.setActive(visit.getActive());
        return response;
    }
}
