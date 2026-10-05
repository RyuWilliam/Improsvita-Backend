package co.improsvita.web.controller;

import co.improsvita.domain.model.Bed;
import co.improsvita.domain.service.BedService;
import co.improsvita.web.dto.BedRequest;
import co.improsvita.web.dto.BedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/beds")
public class BedController {

    private final BedService bedService;

    public BedController(BedService bedService) {
        this.bedService = bedService;
    }

    @GetMapping
    public ResponseEntity<List<BedResponse>> getAllBeds() {
        List<BedResponse> beds = bedService.getAllBeds()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(beds);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BedResponse> getBedById(@PathVariable Integer id) {
        Bed bed = bedService.getBedById(id);
        if (bed == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(bed));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<BedResponse> getBedByCode(@PathVariable String code) {
        Bed bed = bedService.getBedByCode(code);
        if (bed == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(bed));
    }

    @PostMapping
    public ResponseEntity<?> createBed(@RequestBody BedRequest request) {
        try {
            Bed bed = new Bed();
            bed.setCode(request.getCode());
            bed.setMaxCapacity(request.getMaxCapacity());
            bed.setActive(true);

            Bed created = bedService.createBed(bed);
            return ResponseEntity.ok(toResponse(created));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBed(@PathVariable Integer id, @RequestBody BedRequest request) {
        Bed existing = bedService.getBedById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            existing.setCode(request.getCode());
            existing.setMaxCapacity(request.getMaxCapacity());
            if (request.getActive() != null) {
                existing.setActive(request.getActive());
            }

            Bed updated = bedService.updateBed(existing);
            return ResponseEntity.ok(toResponse(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBedById(@PathVariable Integer id) {
        bedService.deleteBedById(id);
        return ResponseEntity.noContent().build();
    }

    private BedResponse toResponse(Bed bed) {
        BedResponse response = new BedResponse();
        response.setId(bed.getId());
        response.setCode(bed.getCode());
        response.setMaxCapacity(bed.getMaxCapacity());
        response.setActive(bed.getActive());
        return response;
    }
}