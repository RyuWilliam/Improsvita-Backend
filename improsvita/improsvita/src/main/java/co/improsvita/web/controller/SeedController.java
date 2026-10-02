package co.improsvita.web.controller;

import co.improsvita.domain.model.Seed;
import co.improsvita.domain.model.SeedType;
import co.improsvita.domain.service.SeedService;
import co.improsvita.web.dto.SeedRequest;
import co.improsvita.web.dto.SeedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/seeds")
public class SeedController {

    private final SeedService seedService;

    public SeedController(SeedService seedService) {
        this.seedService = seedService;
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
    public ResponseEntity<SeedResponse> createSeed(@RequestBody SeedRequest request) {
        Seed seed = new Seed();
        seed.setName(request.getName());
        seed.setType(SeedType.valueOf(request.getType()));
        seed.setDescription(request.getDescription());
        seed.setActive(true);

        Seed created = seedService.createSeed(seed);
        return ResponseEntity.ok(toResponse(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeedResponse> updateSeed(@PathVariable Integer id, @RequestBody SeedRequest request) {
        Seed existing = seedService.getSeedById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setName(request.getName());
        existing.setType(SeedType.valueOf(request.getType()));
        existing.setDescription(request.getDescription());

        Seed updated = seedService.updateSeed(existing);
        return ResponseEntity.ok(toResponse(updated));
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
        SeedType seedType = SeedType.valueOf(type);
        List<SeedResponse> seeds = seedService.getSeedsByType(seedType)
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
        List<SeedResponse> seeds = seedService.getSeedsByStockLess(new BigDecimal(stock))
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(seeds);
    }

    private SeedResponse toResponse(Seed seed) {
        SeedResponse response = new SeedResponse();
        response.setId(seed.getId());
        response.setName(seed.getName());
        response.setType(seed.getType() != null ? seed.getType().name() : null);
        response.setDescription(seed.getDescription());
        response.setTotalAvailable(seed.getTotalAvailable());
        response.setActive(seed.getActive());
        return response;
    }
}