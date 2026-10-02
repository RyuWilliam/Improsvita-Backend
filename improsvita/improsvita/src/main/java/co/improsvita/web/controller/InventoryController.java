package co.improsvita.web.controller;

import co.improsvita.domain.model.SeedLot;
import co.improsvita.domain.model.SeedLotStatus;
import co.improsvita.domain.model.SeedMovement;
import co.improsvita.domain.service.InventoryService;
import co.improsvita.web.dto.AdjustRequest;
import co.improsvita.web.dto.EntryRequest;
import co.improsvita.web.dto.ExitRequest;
import co.improsvita.web.dto.MovementResponse;
import co.improsvita.web.dto.SeedLotResponse;
import co.improsvita.web.dto.StockResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/entries")
    public ResponseEntity<?> registerEntry(@RequestBody EntryRequest request) {
        try {
            SeedLot lot = inventoryService.registerEntry(
                    request.getSeedId(), request.getSupplierId(), request.getLocationId(),
                    request.getLotNumber(), request.getQuantity(),
                    request.getEntryDate(), request.getDueDate());
            return ResponseEntity.ok(toLotResponse(lot));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/lots/{lotId}/exits")
    public ResponseEntity<?> registerExit(@PathVariable Integer lotId, @RequestBody ExitRequest request) {
        try {
            SeedLot lot = inventoryService.registerExit(lotId, request.getQuantity(), request.getReason());
            return ResponseEntity.ok(toLotResponse(lot));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/lots/{lotId}/adjustments")
    public ResponseEntity<?> adjustStock(@PathVariable Integer lotId, @RequestBody AdjustRequest request) {
        try {
            SeedLot lot = inventoryService.adjustStock(lotId, request.getQuantity(), request.getReason());
            return ResponseEntity.ok(toLotResponse(lot));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/lots")
    public ResponseEntity<List<SeedLotResponse>> getLots(
            @RequestParam(required = false) Integer seedId,
            @RequestParam(required = false) Integer locationId,
            @RequestParam(required = false) String status) {
        List<SeedLot> lots;
        if (seedId != null) {
            lots = inventoryService.getLotsBySeed(seedId);
        } else if (locationId != null) {
            lots = inventoryService.getLotsByLocation(locationId);
        } else if (status != null) {
            lots = inventoryService.getLotsByStatus(SeedLotStatus.valueOf(status));
        } else {
            lots = inventoryService.getAllLots();
        }
        return ResponseEntity.ok(lots.stream().map(this::toLotResponse).collect(Collectors.toList()));
    }

    @GetMapping("/lots/{lotId}")
    public ResponseEntity<SeedLotResponse> getLotById(@PathVariable Integer lotId) {
        SeedLot lot = inventoryService.getLotById(lotId);
        if (lot == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toLotResponse(lot));
    }

    @GetMapping("/lots/{lotId}/kardex")
    public ResponseEntity<?> getKardex(@PathVariable Integer lotId) {
        try {
            List<MovementResponse> movements = inventoryService.getKardex(lotId)
                    .stream()
                    .map(this::toMovementResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(movements);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/stock/seed/{seedId}")
    public ResponseEntity<?> getStockBySeed(@PathVariable Integer seedId) {
        try {
            return ResponseEntity.ok(new StockResponse(seedId, inventoryService.getStockBySeedId(seedId)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private SeedLotResponse toLotResponse(SeedLot lot) {
        SeedLotResponse response = new SeedLotResponse();
        response.setId(lot.getId());
        response.setLotNumber(lot.getLotNumber());
        response.setSeedId(lot.getSeedId());
        response.setLocationId(lot.getLocationId());
        response.setEntryDate(lot.getEntryDate());
        response.setDueDate(lot.getDueDate());
        response.setInitialQuantity(lot.getInitialQuantity());
        response.setAvailableQuantity(lot.getAvailableQuantity());
        response.setStatus(lot.getStatus() != null ? lot.getStatus().name() : null);
        return response;
    }

    private MovementResponse toMovementResponse(SeedMovement movement) {
        MovementResponse response = new MovementResponse();
        response.setId(movement.getId());
        response.setLotId(movement.getLotId());
        response.setSupplierId(movement.getSupplierId());
        response.setMovementType(movement.getMovementType() != null ? movement.getMovementType().name() : null);
        response.setQuantity(movement.getQuantity());
        response.setMovementDate(movement.getMovementDate());
        response.setReason(movement.getReason());
        return response;
    }
}