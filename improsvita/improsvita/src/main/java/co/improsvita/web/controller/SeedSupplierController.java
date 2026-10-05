package co.improsvita.web.controller;

import co.improsvita.domain.model.SeedSupplier;
import co.improsvita.domain.service.SeedSupplierService;
import co.improsvita.web.dto.SeedSupplierRequest;
import co.improsvita.web.dto.SeedSupplierResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/seed-suppliers")
public class SeedSupplierController {

    private final SeedSupplierService seedSupplierService;

    public SeedSupplierController(SeedSupplierService seedSupplierService) {
        this.seedSupplierService = seedSupplierService;
    }

    @GetMapping
    public ResponseEntity<List<SeedSupplierResponse>> getLinks(
            @RequestParam(required = false) Integer seedId,
            @RequestParam(required = false) Integer supplierId) {
        List<SeedSupplier> links;
        if (seedId != null) {
            links = seedSupplierService.getBySeedId(seedId);
        } else if (supplierId != null) {
            links = seedSupplierService.getBySupplierId(supplierId);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(links.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<?> link(@RequestBody SeedSupplierRequest request) {
        try {
            SeedSupplier bridge = seedSupplierService.link(request.getSeedId(), request.getSupplierId());
            return ResponseEntity.ok(toResponse(bridge));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> unlink(@RequestParam Integer seedId, @RequestParam Integer supplierId) {
        seedSupplierService.unlink(seedId, supplierId);
        return ResponseEntity.noContent().build();
    }

    private SeedSupplierResponse toResponse(SeedSupplier bridge) {
        SeedSupplierResponse response = new SeedSupplierResponse();
        response.setId(bridge.getId());
        response.setSeedId(bridge.getSeedId());
        response.setSupplierId(bridge.getSupplierId());
        response.setActive(bridge.getActive());
        return response;
    }
}