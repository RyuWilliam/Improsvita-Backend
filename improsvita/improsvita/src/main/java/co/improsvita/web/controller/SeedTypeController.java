package co.improsvita.web.controller;

import co.improsvita.domain.model.SeedTypeCatalog;
import co.improsvita.domain.service.SeedTypeService;
import co.improsvita.web.dto.SeedTypeRequest;
import co.improsvita.web.dto.SeedTypeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/seed-types")
public class SeedTypeController {

    private final SeedTypeService seedTypeService;

    public SeedTypeController(SeedTypeService seedTypeService) {
        this.seedTypeService = seedTypeService;
    }

    @GetMapping
    public ResponseEntity<List<SeedTypeResponse>> getAllSeedTypes() {
        List<SeedTypeResponse> seedTypes = seedTypeService.getAllSeedTypes()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(seedTypes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeedTypeResponse> getSeedTypeById(@PathVariable Integer id) {
        SeedTypeCatalog seedType = seedTypeService.getSeedTypeById(id);
        if (seedType == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(seedType));
    }

    @PostMapping
    public ResponseEntity<?> createSeedType(@RequestBody SeedTypeRequest request) {
        try {
            SeedTypeCatalog created = seedTypeService.createSeedType(request.getName(), request.getDescription());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSeedType(@PathVariable Integer id, @RequestBody SeedTypeRequest request) {
        if (seedTypeService.getSeedTypeById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            SeedTypeCatalog updated = seedTypeService.updateSeedType(id, request.getName(), request.getDescription());
            return ResponseEntity.ok(toResponse(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSeedType(@PathVariable Integer id) {
        if (seedTypeService.getSeedTypeById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            seedTypeService.deleteSeedType(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    private SeedTypeResponse toResponse(SeedTypeCatalog seedType) {
        return new SeedTypeResponse(seedType.getId(), seedType.getName(), seedType.getDescription());
    }
}