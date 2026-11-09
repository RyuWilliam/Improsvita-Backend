package co.improsvita.domain.service;

import co.improsvita.domain.model.Contact;
import co.improsvita.domain.model.ContactRole;
import co.improsvita.domain.model.PageResult;
import co.improsvita.domain.model.Reservation;
import co.improsvita.domain.model.ReservationStatus;
import co.improsvita.domain.model.Sale;
import co.improsvita.domain.model.SaleType;
import co.improsvita.domain.model.Sowing;
import co.improsvita.domain.model.SowingStatus;
import co.improsvita.domain.repository.SaleRepository;
import co.improsvita.domain.repository.SowingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    private static final int CLIENT_ID = 3;
    private static final int SOWING_ID = 3;

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private SowingRepository sowingRepository;

    @Mock
    private ContactService contactService;

    @Mock
    private VisitService visitService;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private SaleService saleService;

    private Sowing sowing;

    @BeforeEach
    void setUp() {
        sowing = new Sowing();
        sowing.setId(SOWING_ID);
        sowing.setQuantitySown(150);
        sowing.setSowingDate(LocalDate.now().minusDays(20));
        sowing.setStatus(SowingStatus.IN_PROGRESS);
        sowing.setActive(true);

        lenient().when(sowingRepository.getById(SOWING_ID)).thenReturn(sowing);
        lenient().when(saleRepository.save(any(Sale.class))).thenAnswer(i -> {
            Sale s = i.getArgument(0);
            s.setId(99);
            return s;
        });
        lenient().when(reservationService.findReservations(anyInt(), any(), any()))
                .thenReturn(List.of());
    }

    private void stubClient() {
        lenient().when(contactService.assertHasRole(CLIENT_ID, ContactRole.CLIENT))
                .thenReturn(new Contact());
    }

    private void stubAvailability(Integer seedlings, int sold) {
        lenient().when(visitService.getLatestAvailableSeedlings(SOWING_ID)).thenReturn(seedlings);
        lenient().when(saleRepository.getSoldQuantity(SOWING_ID)).thenReturn(sold);
    }

    private Sale registerDirect(int quantity, String price) {
        return saleService.registerDirectSale(CLIENT_ID, SOWING_ID, quantity,
                new BigDecimal(price), LocalDate.now(), null);
    }

    // ---------------------------------------------------------------- venta directa

    @Test
    void ventaDirecta_valida_calculaTotalYGuarda() {
        stubClient();
        stubAvailability(120, 0);

        Sale sale = registerDirect(10, "1250.50");

        assertEquals(10, sale.getQuantity());
        assertEquals(SaleType.DIRECT, sale.getSaleType());
        assertNull(sale.getReservationId());
        assertEquals(0, new BigDecimal("12505.00").compareTo(sale.getTotal()));
        assertEquals(true, sale.getActive());
        verify(saleRepository).save(any(Sale.class));
    }

    @Test
    void ventaDirecta_sinFecha_usaHoy() {
        stubClient();
        stubAvailability(120, 0);

        Sale sale = registerDirect(5, "100.00");

        assertEquals(LocalDate.now(), sale.getSaleDate());
    }

    @Test
    void ventaDirecta_exactamenteLoDisponible_esValida() {
        stubClient();
        stubAvailability(120, 20);

        Sale sale = registerDirect(100, "100.00");

        assertEquals(100, sale.getQuantity());
    }

    @Test
    void ventaDirecta_excedeDisponible_lanzaIllegalState() {
        stubClient();
        stubAvailability(120, 20);

        assertThrows(IllegalStateException.class, () -> registerDirect(101, "100.00"));
        verify(saleRepository, never()).save(any());
    }

    @Test
    void ventaDirecta_sinVisitas_disponibleCero() {
        stubClient();
        stubAvailability(null, 0);

        assertThrows(IllegalStateException.class, () -> registerDirect(1, "100.00"));
    }

    @Test
    void ventaDirecta_cantidadCero_lanzaIllegalArgument() {
        stubClient();
        stubAvailability(120, 0);

        assertThrows(IllegalArgumentException.class, () -> registerDirect(0, "100.00"));
    }

    @Test
    void ventaDirecta_cantidadNegativa_lanzaIllegalArgument() {
        stubClient();
        stubAvailability(120, 0);

        assertThrows(IllegalArgumentException.class, () -> registerDirect(-5, "100.00"));
    }

    @Test
    void ventaDirecta_mayorQueSembrada_lanzaIllegalArgument() {
        stubClient();
        stubAvailability(500, 0);

        assertThrows(IllegalArgumentException.class, () -> registerDirect(151, "100.00"));
    }

    @Test
    void ventaDirecta_precioNegativo_lanzaIllegalArgument() {
        stubClient();
        stubAvailability(120, 0);

        assertThrows(IllegalArgumentException.class, () -> registerDirect(10, "-1.00"));
    }

    @Test
    void ventaDirecta_precioConTresDecimales_lanzaIllegalArgument() {
        stubClient();
        stubAvailability(120, 0);

        assertThrows(IllegalArgumentException.class, () -> registerDirect(10, "10.125"));
    }

    @Test
    void ventaDirecta_fechaFutura_lanzaIllegalArgument() {
        stubClient();
        stubAvailability(120, 0);

        assertThrows(IllegalArgumentException.class, () -> saleService.registerDirectSale(
                CLIENT_ID, SOWING_ID, 10, new BigDecimal("100.00"),
                LocalDate.now().plusDays(1), null));
    }

    @Test
    void ventaDirecta_fechaAnteriorALaSiembra_lanzaIllegalArgument() {
        stubClient();
        stubAvailability(120, 0);

        assertThrows(IllegalArgumentException.class, () -> saleService.registerDirectSale(
                CLIENT_ID, SOWING_ID, 10, new BigDecimal("100.00"),
                LocalDate.now().minusDays(30), null));
    }

    @Test
    void ventaDirecta_siembraTerminal_lanzaIllegalState() {
        sowing.setStatus(SowingStatus.COMPLETED);
        stubClient();

        assertThrows(IllegalStateException.class, () -> registerDirect(10, "100.00"));
        verify(saleRepository, never()).save(any());
    }

    @Test
    void ventaDirecta_siembraInactiva_lanzaIllegalState() {
        sowing.setActive(false);
        stubClient();

        assertThrows(IllegalStateException.class, () -> registerDirect(10, "100.00"));
    }

    @Test
    void ventaDirecta_siembraInexistente_lanzaIllegalArgument() {
        when(sowingRepository.getById(999)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> saleService.registerDirectSale(
                CLIENT_ID, 999, 10, new BigDecimal("100.00"), LocalDate.now(), null));
    }

    @Test
    void ventaDirecta_contactoSinRolClient_lanzaIllegalArgument() {
        when(contactService.assertHasRole(CLIENT_ID, ContactRole.CLIENT))
                .thenThrow(new IllegalArgumentException("sin rol"));

        assertThrows(IllegalArgumentException.class, () -> registerDirect(10, "100.00"));
        verify(saleRepository, never()).save(any());
    }

    @Test
    void ventaDirecta_contactoInexistente_lanzaIllegalArgument() {
        when(contactService.assertHasRole(CLIENT_ID, ContactRole.CLIENT))
                .thenThrow(new ContactNotFoundException(CLIENT_ID));

        assertThrows(IllegalArgumentException.class, () -> registerDirect(10, "100.00"));
    }

    @Test
    void ventaDirecta_conReservaViva_lanzaIllegalState() {
        stubClient();
        stubAvailability(120, 0);
        Reservation live = new Reservation();
        live.setId(1);
        live.setSowingId(SOWING_ID);
        live.setReservationStatus(ReservationStatus.PENDING);
        live.setActive(true);
        when(reservationService.findReservations(anyInt(), any(), any())).thenReturn(List.of(live));

        assertThrows(IllegalStateException.class, () -> registerDirect(10, "100.00"));
        verify(saleRepository, never()).save(any());
    }

    @Test
    void ventaDirecta_conReservaCancelada_permiteVender() {
        stubClient();
        stubAvailability(120, 0);
        Reservation cancelled = new Reservation();
        cancelled.setId(1);
        cancelled.setSowingId(SOWING_ID);
        cancelled.setReservationStatus(ReservationStatus.CANCELLED);
        cancelled.setActive(true);
        when(reservationService.findReservations(anyInt(), any(), any()))
                .thenReturn(List.of(cancelled));

        Sale sale = registerDirect(10, "100.00");

        assertEquals(SaleType.DIRECT, sale.getSaleType());
    }

    @Test
    void ventaDirecta_notasVacia_quedaNull() {
        stubClient();
        stubAvailability(120, 0);

        Sale sale = saleService.registerDirectSale(CLIENT_ID, SOWING_ID, 10,
                new BigDecimal("100.00"), LocalDate.now(), "   ");

        assertNull(sale.getNotes());
    }

    @Test
    void ventaDirecta_notasLargas_lanzaIllegalArgument() {
        stubClient();
        stubAvailability(120, 0);

        assertThrows(IllegalArgumentException.class, () -> saleService.registerDirectSale(
                CLIENT_ID, SOWING_ID, 10, new BigDecimal("100.00"), LocalDate.now(),
                "x".repeat(501)));
    }

    // ---------------------------------------------------------------- venta de reserva

    @Test
    void ventaDeReserva_valida_tomaClienteYSiembraDeLaReserva() {
        Reservation reservation = new Reservation();
        reservation.setId(7);
        reservation.setSowingId(SOWING_ID);
        reservation.setClientId(CLIENT_ID);
        reservation.setActive(true);
        when(reservationService.getReservationById(7)).thenReturn(reservation);
        when(saleRepository.getByReservationId(7)).thenReturn(null);
        stubClient();
        stubAvailability(120, 0);

        Sale sale = saleService.registerReservationSale(7, 10, new BigDecimal("100.00"),
                LocalDate.now(), null);

        assertEquals(CLIENT_ID, sale.getContactId());
        assertEquals(SOWING_ID, sale.getSowingId());
        assertEquals(7, sale.getReservationId());
        assertEquals(SaleType.RESERVATION, sale.getSaleType());
    }

    @Test
    void ventaDeReserva_yaFacturada_lanzaIllegalState() {
        Reservation reservation = new Reservation();
        reservation.setId(7);
        reservation.setSowingId(SOWING_ID);
        reservation.setClientId(CLIENT_ID);
        when(reservationService.getReservationById(7)).thenReturn(reservation);
        Sale existing = new Sale();
        existing.setId(50);
        when(saleRepository.getByReservationId(7)).thenReturn(existing);

        assertThrows(IllegalStateException.class, () -> saleService.registerReservationSale(
                7, 10, new BigDecimal("100.00"), LocalDate.now(), null));
        verify(saleRepository, never()).save(any());
    }

    @Test
    void ventaDeReserva_inexistente_lanzaIllegalArgument() {
        when(reservationService.getReservationById(404)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> saleService.registerReservationSale(
                404, 10, new BigDecimal("100.00"), LocalDate.now(), null));
    }

    @Test
    void ventaDeReserva_sinId_lanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> saleService.registerReservationSale(
                null, 10, new BigDecimal("100.00"), LocalDate.now(), null));
    }

    // ---------------------------------------------------------------- anulacion

    @Test
    void cancelarVenta_laMarcaInactiva() {
        Sale sale = new Sale();
        sale.setId(20);
        sale.setActive(true);
        when(saleRepository.getById(20)).thenReturn(sale);

        Sale cancelled = saleService.cancelSale(20);

        assertEquals(false, cancelled.getActive());
        assertTrue(cancelled.isCancelled());
    }

    @Test
    void cancelarVenta_yaAnulada_lanzaIllegalState() {
        Sale sale = new Sale();
        sale.setId(20);
        sale.setActive(false);
        when(saleRepository.getById(20)).thenReturn(sale);

        assertThrows(IllegalStateException.class, () -> saleService.cancelSale(20));
    }

    @Test
    void cancelarVenta_inexistente_lanzaIllegalArgument() {
        when(saleRepository.getById(404)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> saleService.cancelSale(404));
    }

    // ---------------------------------------------------------------- disponibilidad e historial

    @Test
    void disponibilidad_descuentaLoVendido() {
        stubAvailability(120, 45);

        assertEquals(75, saleService.getAvailability(SOWING_ID));
    }

    @Test
    void disponibilidad_sinVisitas_esCero() {
        stubAvailability(null, 0);

        assertEquals(0, saleService.getAvailability(SOWING_ID));
    }

    @Test
    void disponibilidad_nuncaEsNegativa() {
        stubAvailability(10, 50);

        assertEquals(0, saleService.getAvailability(SOWING_ID));
    }

    @Test
    void historial_rangoInvertido_lanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> saleService.findSales(
                null, null, LocalDate.now(), LocalDate.now().minusDays(5)));
    }

    @Test
    void historial_sinFiltros_delegaEnElRepositorio() {
        when(saleRepository.getAll(null, null, null, null)).thenReturn(List.of(new Sale()));

        assertEquals(1, saleService.findSales(null, null, null, null).size());
    }

    @Test
    void historialPaginado_valido_delegaEnElRepositorio() {
        PageResult<Sale> page = new PageResult<>(List.of(new Sale()), 0, 20, 1, 1);
        when(saleRepository.getPage(null, null, null, null, 0, 20)).thenReturn(page);

        PageResult<Sale> result = saleService.findSalesPage(null, null, null, null, 0, 20);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void historialPaginado_paginaNegativa_lanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> saleService.findSalesPage(null, null, null, null, -1, 20));
    }

    @Test
    void historialPaginado_tamanoCero_lanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> saleService.findSalesPage(null, null, null, null, 0, 0));
    }

    @Test
    void historialPaginado_tamanoExcesivo_lanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> saleService.findSalesPage(null, null, null, null, 0, 101));
        verify(saleRepository, never()).getPage(any(), any(), any(), any(), anyInt(), anyInt());
    }

    @Test
    void historialPaginado_rangoInvertido_lanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> saleService.findSalesPage(
                null, null, LocalDate.now(), LocalDate.now().minusDays(1), 0, 20));
    }

    @Test
    void disponibilidadDetalle_devuelveElDesglose() {
        stubAvailability(120, 30);

        SaleService.Availability detail = saleService.getAvailabilityDetail(SOWING_ID);

        assertEquals(SOWING_ID, detail.sowingId());
        assertEquals(120, detail.seedlingsAvailable());
        assertEquals(30, detail.soldQuantity());
        assertEquals(90, detail.available());
    }

    @Test
    void disponibilidadDetalle_sinVisitas_disponibleCero() {
        stubAvailability(null, 0);

        SaleService.Availability detail = saleService.getAvailabilityDetail(SOWING_ID);

        assertEquals(0, detail.available());
        assertNull(detail.seedlingsAvailable());
    }
}
