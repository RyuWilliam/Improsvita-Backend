package co.improsvita.web.controller;

import co.improsvita.domain.model.PageResult;
import co.improsvita.domain.model.Sale;
import co.improsvita.domain.model.SaleType;
import co.improsvita.domain.service.SaleService;
import co.improsvita.web.dto.AvailabilityResponse;
import co.improsvita.web.dto.ReservationSaleRequest;
import co.improsvita.web.dto.SalePageResponse;
import co.improsvita.web.dto.SaleRequest;
import co.improsvita.web.dto.SaleResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sales")
public class SaleController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    /** Venta directa sobre una siembra. */
    @PostMapping
    public ResponseEntity<?> registerDirectSale(@Valid @RequestBody SaleRequest request) {
        try {
            Sale sale = saleService.registerDirectSale(
                    request.getContactId(), request.getSowingId(), request.getQuantity(),
                    request.getUnitPrice(), request.getSaleDate(), request.getNotes());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(sale));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /** Entrega de una reserva: cliente y siembra salen de la reserva. */
    @PostMapping("/reservation")
    public ResponseEntity<?> registerReservationSale(@Valid @RequestBody ReservationSaleRequest request) {
        try {
            Sale sale = saleService.registerReservationSale(
                    request.getReservationId(), request.getQuantity(),
                    request.getUnitPrice(), request.getSaleDate(), request.getNotes());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(sale));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /** Historial completo (sin paginar). Útil para exportar. */
    @GetMapping
    public ResponseEntity<?> getSales(
            @RequestParam(required = false) Integer contactId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        try {
            List<SaleResponse> sales = saleService.findSales(contactId, parseType(type), from, to)
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(sales);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** Historial paginado con filtros de cliente, fechas y tipo. */
    @GetMapping("/page")
    public ResponseEntity<?> getSalesPage(
            @RequestParam(required = false) Integer contactId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size) {
        try {
            PageResult<Sale> result = saleService.findSalesPage(
                    contactId, parseType(type), from, to, page, size);
            return ResponseEntity.ok(toPageResponse(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponse> getSaleById(@PathVariable Integer id) {
        Sale sale = saleService.getSaleById(id);
        if (sale == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(sale));
    }

    /** Plántulas disponibles para vender en una siembra. */
    @GetMapping("/availability/{sowingId}")
    public ResponseEntity<?> getAvailability(@PathVariable Integer sowingId) {
        try {
            SaleService.Availability availability = saleService.getAvailabilityDetail(sowingId);
            return ResponseEntity.ok(new AvailabilityResponse(
                    availability.sowingId(), availability.seedlingsAvailable(),
                    availability.soldQuantity(), availability.available()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /** Anula una venta (queda en el historial como inactiva). */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelSale(@PathVariable Integer id) {
        try {
            saleService.cancelSale(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    private SaleType parseType(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }
        try {
            return SaleType.valueOf(type.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de venta inválido: " + type
                    + ". Permitidos: DIRECT, RESERVATION");
        }
    }

    private SaleResponse toResponse(Sale sale) {
        SaleResponse response = new SaleResponse();
        response.setId(sale.getId());
        response.setContactId(sale.getContactId());
        response.setSowingId(sale.getSowingId());
        response.setReservationId(sale.getReservationId());
        response.setSaleDate(sale.getSaleDate());
        response.setQuantity(sale.getQuantity());
        response.setUnitPrice(sale.getUnitPrice());
        response.setTotal(sale.getTotal());
        response.setSaleType(sale.getSaleType() != null ? sale.getSaleType().name() : null);
        response.setNotes(sale.getNotes());
        response.setActive(sale.getActive());
        return response;
    }

    private SalePageResponse toPageResponse(PageResult<Sale> page) {
        return new SalePageResponse(
                page.getContent().stream().map(this::toResponse).collect(Collectors.toList()),
                page.getPage(), page.getSize(), page.getTotalElements(),
                page.getTotalPages(), page.hasNext(), page.hasPrevious());
    }
}
