package co.improsvita.domain.service;

import co.improsvita.domain.model.Contact;
import co.improsvita.domain.model.ContactRole;
import co.improsvita.domain.model.Reservation;
import co.improsvita.domain.model.ReservationStatus;
import co.improsvita.domain.model.Sowing;
import co.improsvita.domain.model.SowingStatus;
import co.improsvita.domain.repository.ReservationRepository;
import co.improsvita.domain.repository.SaleRepository;
import co.improsvita.domain.repository.SowingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    private static final int RESERVATION_ID = 1;
    private static final int SOWING_ID = 1;
    private static final int OTHER_SOWING_ID = 2;
    private static final int CLIENT_ID = 3;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SowingRepository sowingRepository;

    @Mock
    private ContactService contactService;

    @Mock
    private SaleRepository saleRepository;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(
                reservationRepository, sowingRepository, contactService, saleRepository);
        lenient().when(contactService.assertHasRole(CLIENT_ID, ContactRole.CLIENT))
                .thenReturn(new Contact());
        lenient().when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(i -> i.getArgument(0));
    }

    private Sowing sowing(int id, SowingStatus status) {
        Sowing sowing = new Sowing();
        sowing.setId(id);
        sowing.setStatus(status);
        sowing.setActive(true);
        sowing.setQuantitySown(100);
        sowing.setSowingDate(LocalDate.now().minusDays(20));
        return sowing;
    }

    private Reservation reservation(ReservationStatus status) {
        Reservation reservation = new Reservation();
        reservation.setId(RESERVATION_ID);
        reservation.setSowingId(SOWING_ID);
        reservation.setClientId(CLIENT_ID);
        reservation.setReservationStatus(status);
        reservation.setTotalPrice(new BigDecimal("450000.00"));
        reservation.setActive(true);
        return reservation;
    }

    @Test
    void update_reservaViva_enSiembraFailed_permiteEditar() {
        Reservation existing = reservation(ReservationStatus.PENDING);
        when(reservationRepository.getById(RESERVATION_ID)).thenReturn(existing);
        when(sowingRepository.getById(SOWING_ID)).thenReturn(sowing(SOWING_ID, SowingStatus.FAILED));
        when(reservationRepository.getBySowingId(SOWING_ID)).thenReturn(List.of(existing));

        Reservation updated = reservationService.updateReservation(
                RESERVATION_ID, null, null, null, new BigDecimal("450000.00"),
                ReservationStatus.CONFIRMED, "Actualizada", null);

        assertEquals(ReservationStatus.CONFIRMED, updated.getReservationStatus());
        assertEquals("Actualizada", updated.getNotes());
        verify(reservationRepository).save(existing);
    }

    @Test
    void update_reservaCanceladaAConfirmada_enSiembraFailed_lanzaIllegalState() {
        Reservation existing = reservation(ReservationStatus.CANCELLED);
        when(reservationRepository.getById(RESERVATION_ID)).thenReturn(existing);
        when(sowingRepository.getById(SOWING_ID)).thenReturn(sowing(SOWING_ID, SowingStatus.FAILED));

        assertThrows(IllegalStateException.class, () -> reservationService.updateReservation(
                RESERVATION_ID, null, null, null, null, ReservationStatus.CONFIRMED, null, null));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void update_reservaViva_cambiandoASiembraFailed_lanzaIllegalState() {
        Reservation existing = reservation(ReservationStatus.CONFIRMED);
        when(reservationRepository.getById(RESERVATION_ID)).thenReturn(existing);
        when(sowingRepository.getById(OTHER_SOWING_ID))
                .thenReturn(sowing(OTHER_SOWING_ID, SowingStatus.FAILED));

        assertThrows(IllegalStateException.class, () -> reservationService.updateReservation(
                RESERVATION_ID, OTHER_SOWING_ID, null, null, null, null, null, null));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void create_enSiembraFailed_lanzaIllegalState() {
        when(sowingRepository.getById(SOWING_ID)).thenReturn(sowing(SOWING_ID, SowingStatus.FAILED));

        assertThrows(IllegalStateException.class, () -> reservationService.createReservation(
                SOWING_ID, CLIENT_ID, LocalDate.now(), new BigDecimal("450000.00"),
                ReservationStatus.PENDING, null));
        verify(reservationRepository, never()).save(any());
    }
}
