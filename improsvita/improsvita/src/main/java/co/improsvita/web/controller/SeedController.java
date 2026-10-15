package co.improsvita.web.controller;

import co.improsvita.domain.model.Seed;
import co.improsvita.domain.model.SeedTypeCatalog;
import co.improsvita.domain.service.SeedService;
import co.improsvita.domain.service.SeedTypeService;
import co.improsvita.web.dto.SeedRequest;
import co.improsvita.web.dto.SeedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/seeds")
public class SeedController {

    private final SeedService seedService;
    private final SeedTypeService seedTypeService;

    public SeedController(SeedService seedService, SeedTypeService seedTypeService) {
        this.seedService = seedService;
        this.seedTypeService = seedTypeService;
    }

    @GetMapping
    public ResponseEntity<List<SeedResponse>> getAllSeeds() {
        List<SeedResponse> seeds = seedService.getAllSeeds()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(seeds);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeedResponse> getSeedById(@PathVariable Integer id) {
        Seed seed = seedService.getSeedById(id);
        if (seed == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(seed));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<SeedResponse> getSeedByName(@PathVariable String name) {
        Seed seed = seedService.getSeedByName(name);
        if (seed == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(seed));
    }

    @PostMapping
    public ResponseEntity<?> createSeed(@RequestBody SeedRequest request) {
        try {
            SeedTypeCatalog seedType = seedTypeService.resolveSeedType(request.getTypeId(), request.getType());
            if (seedType == null) {
                throw new IllegalArgumentException("El tipo de semilla es obligatorio (typeId o type)");
            }

            Seed seed = new Seed();
            seed.setName(request.getName());
            seed.setSeedType(seedType);
            seed.setDescription(request.getDescription());
            seed.setActive(true);

            Seed created = seedService.createSeed(seed);
            return ResponseEntity.ok(toResponse(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSeed(@PathVariable Integer id, @RequestBody SeedRequest request) {
        Seed existing = seedService.getSeedById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            SeedTypeCatalog seedType = seedTypeService.resolveSeedType(request.getTypeId(), request.getType());

            existing.setName(request.getName());
            if (seedType != null) {
                existing.setSeedType(seedType);
            }
            existing.setDescription(request.getDescription());

            Seed updated = seedService.updateSeed(existing);
            return ResponseEntity.ok(toResponse(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeedById(@PathVariable Integer id) {
        seedService.deleteSeedById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/name/{name}")
    public ResponseEntity<Void> deleteSeedByName(@PathVariable String name) {
        seedService.deleteSeedByName(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<SeedResponse>> getSeedsByType(@PathVariable String type) {
        List<SeedResponse> seeds = seedService.getSeedsByType(type)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(seeds);
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<SeedResponse>> getSeedsBySupplier(@PathVariable Integer supplierId) {
        List<SeedResponse> seeds = seedService.getSeedsBySupplierId(supplierId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(seeds);
    }

    @GetMapping("/stock-less/{stock}")
    public ResponseEntity<List<SeedResponse>> getSeedsByStockLess(@PathVariable String stock) {
        List<SeedResponse> seeds = seedService.getSeedsByStockLess(Long.valueOf(stock))
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(seeds);
    }

    private SeedResponse toResponse(Seed seed) {
        SeedResponse response = new SeedResponse();
        response.setId(seed.getId());
        response.setName(seed.getName());
        response.setTypeId(seed.getSeedType() != null ? seed.getSeedType().getId() : null);
        response.setType(seed.getSeedType() != null ? seed.getSeedType().getName() : null);
        response.setDescription(seed.getDescription());
        response.setTotalAvailable(seed.getTotalAvailable());
        response.setActive(seed.getActive());
        return response;
    }
}