package co.improsvita.domain.service;

import co.improsvita.domain.model.Reservation;
import co.improsvita.domain.model.ReservationStatus;
import co.improsvita.domain.model.Sowing;
import co.improsvita.domain.model.SowingStatus;
import co.improsvita.domain.repository.ClientRepository;
import co.improsvita.domain.repository.ReservationRepository;
import co.improsvita.domain.repository.SowingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    /** Solo se reserva producción que todavía no terminó. */
    private static final Set<SowingStatus> RESERVABLE = Set.of(
            SowingStatus.PLANNED, SowingStatus.IN_PROGRESS);

    private final ReservationRepository reservationRepository;
    private final SowingRepository sowingRepository;
    private final ClientRepository clientRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              SowingRepository sowingRepository,
                              ClientRepository clientRepository) {
        this.reservationRepository = reservationRepository;
        this.sowingRepository = sowingRepository;
        this.clientRepository = clientRepository;
    }

    /**
     * Combinación de filtros para el listado. Con un solo filtro se usa el finder
     * indexado; con varios se combinan en memoria (la tabla es pequeña).
     */
    public List<Reservation> findReservations(Integer sowingId, Integer clientId, ReservationStatus status) {
        if (sowingId != null && clientId == null && status == null) {
            return reservationRepository.getBySowingId(sowingId);
        }
        if (clientId != null && sowingId == null && status == null) {
            return reservationRepository.getByClientId(clientId);
        }
        return reservationRepository.getAll().stream()
                .filter(reservation -> sowingId == null || sowingId.equals(reservation.getSowingId()))
                .filter(reservation -> clientId == null || clientId.equals(reservation.getClientId()))
                .filter(reservation -> status == null || status == reservation.getReservationStatus())
                .collect(Collectors.toList());
    }

    public Reservation getReservationById(Integer id) {
        return reservationRepository.getById(id);
    }

    @Transactional
    public Reservation createReservation(Integer sowingId, Integer clientId, LocalDate reservationDate,
                                         BigDecimal totalPrice, ReservationStatus status, String notes) {
        ReservationStatus resultingStatus = status != null ? status : ReservationStatus.PENDING;

        validate(sowingId, clientId, totalPrice, resultingStatus, Boolean.TRUE, null);

        Reservation reservation = new Reservation();
        reservation.setSowingId(sowingId);
        reservation.setClientId(clientId);
        reservation.setReservationDate(reservationDate != null ? reservationDate : LocalDate.now());
        reservation.setTotalPrice(totalPrice);
        reservation.setReservationStatus(resultingStatus);
        reservation.setNotes(notes);
        reservation.setActive(true);
        return reservationRepository.save(reservation);
    }

    /**
     * Actualiza solo lo que llega; lo que venga null se conserva. La validación se
     * hace sobre el estado resultante, no sobre el actual: pasar una reserva cancelada
     * a CONFIRMED vuelve a competir por la siembra.
     */
    @Transactional
    public Reservation updateReservation(Integer id, Integer sowingId, Integer clientId,
                                         LocalDate reservationDate, BigDecimal totalPrice,
                                         ReservationStatus status, String notes, Boolean active) {
        Reservation reservation = requireReservation(id);

        Integer targetSowingId = sowingId != null ? sowingId : reservation.getSowingId();
        Integer targetClientId = clientId != null ? clientId : reservation.getClientId();
        BigDecimal targetPrice = totalPrice != null ? totalPrice : reservation.getTotalPrice();
        ReservationStatus targetStatus = status != null ? status : reservation.getReservationStatus();
        Boolean targetActive = active != null ? active : reservation.getActive();

        validate(targetSowingId, targetClientId, targetPrice, targetStatus, targetActive, id);

        reservation.setSowingId(targetSowingId);
        reservation.setClientId(targetClientId);
        reservation.setTotalPrice(targetPrice);
        reservation.setReservationStatus(targetStatus);
        reservation.setActive(targetActive);
        if (reservationDate != null) {
            reservation.setReservationDate(reservationDate);
        }
        if (notes != null) {
            reservation.setNotes(notes);
        }
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation changeStatus(Integer id, ReservationStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("El estado de la reserva es obligatorio");
        }
        return updateReservation(id, null, null, null, null, status, null, null);
    }

    @Transactional
    public void deleteReservationById(Integer id) {
        requireReservation(id);
        reservationRepository.deleteById(id);
    }

    /**
     * Reglas de negocio:
     *  - la siembra y el cliente deben existir;
     *  - una siembra admite una sola reserva viva (cancelada o inactiva la libera);
     *  - una reserva viva solo se permite sobre siembras PLANNED o IN_PROGRESS;
     *  - cancelar o inactivar siempre está permitido, sin importar el estado de la siembra.
     */
    private void validate(Integer sowingId, Integer clientId, BigDecimal totalPrice,
                          ReservationStatus status, Boolean active, Integer ownReservationId) {
        Sowing sowing = sowingRepository.getById(sowingId);
        if (sowing == null) {
            throw new IllegalArgumentException("Siembra no encontrada: " + sowingId);
        }
        if (clientRepository.getById(clientId) == null) {
            throw new IllegalArgumentException("Cliente no encontrado: " + clientId);
        }
        if (totalPrice == null || totalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio total es obligatorio y no puede ser negativo");
        }

        boolean live = Boolean.TRUE.equals(active) && status != ReservationStatus.CANCELLED;
        if (!live) {
            return;
        }

        if (!RESERVABLE.contains(sowing.getStatus())) {
            throw new IllegalStateException("No se puede reservar la siembra " + sowingId
                    + " porque está en estado " + sowing.getStatus()
                    + ". Solo se permite reservar siembras PLANNED o IN_PROGRESS");
        }

        reservationRepository.getBySowingId(sowingId).stream()
                .filter(Reservation::isLive)
                .filter(existing -> !existing.getId().equals(ownReservationId))
                .findFirst()
                .ifPresent(existing -> {
                    throw new IllegalStateException("La siembra " + sowingId
                            + " ya tiene una reserva vigente (id " + existing.getId()
                            + "). Cancélala antes de crear otra.");
                });
    }

    private Reservation requireReservation(Integer id) {
        Reservation reservation = reservationRepository.getById(id);
        if (reservation == null) {
            throw new IllegalArgumentException("Reserva no encontrada: " + id);
        }
        return reservation;
    }
}
