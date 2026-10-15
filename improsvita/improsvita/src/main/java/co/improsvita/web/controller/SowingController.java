package co.improsvita.web.controller;

import co.improsvita.domain.service.SowingReviewService;
import co.improsvita.domain.model.Sowing;
import co.improsvita.domain.model.SowingStatus;
import co.improsvita.domain.service.InventoryService;
import co.improsvita.domain.service.SowingService;
import co.improsvita.web.dto.SowRequest;
import co.improsvita.web.dto.SowingResponse;
import co.improsvita.web.dto.SowingStatusRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sowings")
public class SowingController {

    private final InventoryService inventoryService;
    private final SowingService sowingService;
    private final SowingReviewService sowingReviewService;

    public SowingController(InventoryService inventoryService, SowingService sowingService,
                            SowingReviewService sowingReviewService) {
        this.inventoryService = inventoryService;
        this.sowingService = sowingService;
        this.sowingReviewService = sowingReviewService;
    }

    @PostMapping
    public ResponseEntity<?> sow(@RequestBody SowRequest request) {
        try {
            Sowing sowing = inventoryService.sow(
                    request.getLotId(), request.getBedId(), request.getQuantity(),
                    request.getSowingDate(), request.getExpectedGerminationDate(), request.getNotes(),
                    parseStatus(request.getStatus()));
            return ResponseEntity.ok(toResponse(sowing));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<SowingResponse>> getSowings(
            @RequestParam(required = false) Integer lotId,
            @RequestParam(required = false) Integer bedId,
            @RequestParam(required = false) String bedCode,
            @RequestParam(required = false) String status) {
        List<Sowing> sowings;
        if (lotId != null) {
            sowings = sowingService.getSowingsByLot(lotId);
        } else if (bedId != null) {
            sowings = sowingService.getSowingsByBed(bedId);
        } else if (bedCode != null) {
            sowings = sowingService.getSowingsByBedCode(bedCode);
        } else if (status != null) {
            sowings = sowingService.getSowingsByStatus(SowingStatus.valueOf(status));
        } else {
            sowings = sowingService.getAllSowings();
        }
        return ResponseEntity.ok(sowings.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SowingResponse> getSowingById(@PathVariable Integer id) {
        Sowing sowing = sowingService.getSowingById(id);
        if (sowing == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(sowing));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(@PathVariable Integer id, @RequestBody SowingStatusRequest request) {
        try {
            Sowing sowing = sowingService.changeStatus(id, SowingStatus.valueOf(request.getStatus()));
            return ResponseEntity.ok(toResponse(sowing));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSowingById(@PathVariable Integer id) {
        sowingService.deleteSowingById(id);
        return ResponseEntity.noContent().build();
    }

    private SowingStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return SowingStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de siembra inválido: " + status);
        }
    }

    private SowingResponse toResponse(Sowing sowing) {
        SowingResponse response = new SowingResponse();
        response.setId(sowing.getId());
        response.setLotId(sowing.getLotId());
        response.setBedId(sowing.getBedId());
        response.setQuantitySown(sowing.getQuantitySown());
        response.setSowingDate(sowing.getSowingDate());
        response.setExpectedGerminationDate(sowing.getExpectedGerminationDate());
        response.setStatus(sowing.getStatus() != null ? sowing.getStatus().name() : null);
        response.setNotes(sowing.getNotes());
        response.setActive(sowing.getActive());
        response.setGerminatedQuantity(sowingReviewService.getLatestGerminatedQuantity(sowing.getId()));
        return response;
    }
}