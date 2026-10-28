package co.improsvita.web.controller;

import co.improsvita.domain.model.Reservation;
import co.improsvita.domain.model.ReservationStatus;
import co.improsvita.domain.service.ReservationService;
import co.improsvita.web.dto.ReservationRequest;
import co.improsvita.web.dto.ReservationResponse;
import co.improsvita.web.dto.ReservationStatusRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservations(
            @RequestParam(required = false) Integer sowingId,
            @RequestParam(required = false) Integer clientId,
            @RequestParam(required = false) String status) {
        List<ReservationResponse> reservations = reservationService
                .findReservations(sowingId, clientId, parseStatus(status))
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Integer id) {
        Reservation reservation = reservationService.getReservationById(id);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(reservation));
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        Reservation created = reservationService.createReservation(
                request.getSowingId(), request.getClientId(), request.getReservationDate(),
                request.getTotalPrice(), parseStatus(request.getReservationStatus()), request.getNotes());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(@PathVariable Integer id,
                                                                 @Valid @RequestBody ReservationRequest request) {
        Reservation updated = reservationService.updateReservation(
                id, request.getSowingId(), request.getClientId(), request.getReservationDate(),
                request.getTotalPrice(), parseStatus(request.getReservationStatus()),
                request.getNotes(), request.getActive());
        return ResponseEntity.ok(toResponse(updated));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ReservationResponse> changeStatus(@PathVariable Integer id,
                                                            @RequestBody ReservationStatusRequest request) {
        Reservation updated = reservationService.changeStatus(id, parseStatus(request.getStatus()));
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationById(@PathVariable Integer id) {
        reservationService.deleteReservationById(id);
        return ResponseEntity.noContent().build();
    }

    private ReservationStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return ReservationStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de reserva inválido: " + status
                    + ". Permitidos: PENDING, CONFIRMED, DELIVERED, CANCELLED");
        }
    }

    private ReservationResponse toResponse(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        response.setId(reservation.getId());
        response.setSowingId(reservation.getSowingId());
        response.setClientId(reservation.getClientId());
        response.setReservationDate(reservation.getReservationDate());
        response.setTotalPrice(reservation.getTotalPrice());
        response.setReservationStatus(reservation.getReservationStatus() != null
                ? reservation.getReservationStatus().name() : null);
        response.setNotes(reservation.getNotes());
        response.setActive(reservation.getActive());
        return response;
    }
}
