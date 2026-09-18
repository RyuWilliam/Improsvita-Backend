package co.improsvita.web.controller;

import co.improsvita.domain.model.Seed;
import co.improsvita.domain.model.Supplier;
import co.improsvita.domain.service.SeedService;
import co.improsvita.persistence.crud.SupplierJpaRepository;
import co.improsvita.persistence.mapper.ProviderMapper;
import co.improsvita.web.dto.SeedRequest;
import co.improsvita.web.dto.SeedResponse;
import co.improsvita.web.dto.SupplierResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/seeds")
public class SeedController {

    private final SeedService seedService;
    private final SupplierJpaRepository supplierJpaRepository;

    public SeedController(SeedService seedService, SupplierJpaRepository supplierJpaRepository) {
        this.seedService = seedService;
        this.supplierJpaRepository = supplierJpaRepository;
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
        Supplier supplier = getSupplierFromRequest(request.getSupplierId());
        if (supplier == null) {
            return ResponseEntity.badRequest().build();
        }

        Seed seed = new Seed();
        seed.setName(request.getName());
        seed.setSupplier(supplier);
        seed.setQuantity(request.getQuantity());
        seed.setType(Seed.SeedType.valueOf(request.getType()));
        seed.setAcquisitionDate(request.getAcquisitionDate());
        seed.setExpirationDate(request.getExpirationDate());
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

        Supplier supplier = getSupplierFromRequest(request.getSupplierId());
        if (supplier == null) {
            return ResponseEntity.badRequest().build();
        }

        existing.setName(request.getName());
        existing.setSupplier(supplier);
        existing.setQuantity(request.getQuantity());
        existing.setType(Seed.SeedType.valueOf(request.getType()));
        existing.setAcquisitionDate(request.getAcquisitionDate());
        existing.setExpirationDate(request.getExpirationDate());

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
        Seed.SeedType seedType = Seed.SeedType.valueOf(type);
        List<SeedResponse> seeds = seedService.getSeedsByType(seedType)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(seeds);
    }

    @GetMapping("/stock-less/{stock}")
    public ResponseEntity<List<SeedResponse>> getSeedsByStockLess(@PathVariable Integer stock) {
        List<SeedResponse> seeds = seedService.getSeedsByStockLess(stock)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(seeds);
    }

    private SeedResponse toResponse(Seed seed) {
        SupplierResponse supplierResponse = new SupplierResponse();
        supplierResponse.setId(seed.getSupplier().getId());
        supplierResponse.setName(seed.getSupplier().getName());
        supplierResponse.setPhone(seed.getSupplier().getPhone());
        supplierResponse.setEmail(seed.getSupplier().getEmail());

        SeedResponse response = new SeedResponse();
        response.setId(seed.getId());
        response.setName(seed.getName());
        response.setSupplier(supplierResponse);
        response.setQuantity(seed.getQuantity());
        response.setType(seed.getType().name());
        response.setAcquisitionDate(seed.getAcquisitionDate());
        response.setExpirationDate(seed.getExpirationDate());
        response.setActive(seed.getActive());
        return response;
    }

    private Supplier getSupplierFromRequest(Integer supplierId) {
        return supplierJpaRepository.findById(supplierId)
                .map(ProviderMapper::toDomain)
                .orElse(null);
    }
}
