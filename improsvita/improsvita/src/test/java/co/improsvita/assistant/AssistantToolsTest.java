package co.improsvita.assistant;

import co.improsvita.domain.model.Bed;
import co.improsvita.domain.model.Contact;
import co.improsvita.domain.model.ContactRole;
import co.improsvita.domain.model.Reservation;
import co.improsvita.domain.model.ReservationStatus;
import co.improsvita.domain.model.Sale;
import co.improsvita.domain.model.Seed;
import co.improsvita.domain.model.SeedLot;
import co.improsvita.domain.model.SeedLotStatus;
import co.improsvita.domain.model.SeedTypeCatalog;
import co.improsvita.domain.model.Sowing;
import co.improsvita.domain.model.SowingStatus;
import co.improsvita.domain.service.BedService;
import co.improsvita.domain.service.ContactService;
import co.improsvita.domain.service.InventoryService;
import co.improsvita.domain.service.ReservationService;
import co.improsvita.domain.service.SaleService;
import co.improsvita.domain.service.SeedService;
import co.improsvita.domain.service.SowingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssistantToolsTest {

    @Mock
    private SeedService seedService;
    @Mock
    private SowingService sowingService;
    @Mock
    private SaleService saleService;
    @Mock
    private ReservationService reservationService;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private ContactService contactService;
    @Mock
    private BedService bedService;

    private AssistantToolCallTracker tracker;
    private AssistantTools tools;

    @BeforeEach
    void setUp() {
        tracker = new AssistantToolCallTracker();
        tools = new AssistantTools(
                seedService,
                sowingService,
                saleService,
                reservationService,
                inventoryService,
                contactService,
                bedService,
                tracker,
                20);
    }

    @Test
    void consultarDisponibilidadFiltraPorSemillaYSumaDisponibles() {
        Seed tomate = seed(1, "Tomate", 500L);
        Seed lechuga = seed(2, "Lechuga", 80L);
        SeedLot tomateLot = lot(10, 1, LocalDate.now().plusDays(10), 200,
                SeedLotStatus.AVAILABLE);
        SeedLot lechugaLot = lot(20, 2, LocalDate.now().plusDays(10), 100,
                SeedLotStatus.AVAILABLE);
        Sowing tomateSowing = sowing(1, 10, 1, SowingStatus.IN_PROGRESS);
        Sowing lechugaSowing = sowing(2, 20, 2, SowingStatus.PLANNED);

        stubSnapshot(
                List.of(tomate, lechuga),
                List.of(tomateLot, lechugaLot),
                List.of(tomateSowing, lechugaSowing),
                List.of(bed(1, "Cama 1"), bed(2, "Cama 2")),
                List.of());
        when(saleService.getAvailabilityDetail(1))
                .thenReturn(new SaleService.Availability(1, 120, 20, 100));

        AssistantTools.DisponibilidadPlantulas result =
                tools.consultarDisponibilidadPlantulas("tomate");

        assertEquals(100, result.totalPlantulasDisponibles());
        assertEquals(1, result.semillas().size());
        assertEquals("Tomate", result.semillas().getFirst().semilla());
        assertEquals(100, result.semillas().getFirst().plantulasDisponibles());
        assertEquals("Cama 1", result.semillas().getFirst().siembras().getFirst().cama());
        assertTrue(tracker.finish().contains("disponibilidad"));
    }

    @Test
    void listarReservasVigentesExcluyeCanceladasYResuelveNombres() {
        Seed tomate = seed(1, "Tomate", 500L);
        SeedLot tomateLot = lot(10, 1, LocalDate.now().plusDays(10), 200,
                SeedLotStatus.AVAILABLE);
        Sowing sowing = sowing(1, 10, 1, SowingStatus.IN_PROGRESS);
        Contact client = contact(7, "Cliente Uno");

        stubSnapshot(
                List.of(tomate),
                List.of(tomateLot),
                List.of(sowing),
                List.of(bed(1, "Cama 1")),
                List.of(client));

        Reservation live = reservation(100, 1, 7, LocalDate.now().minusDays(1),
                ReservationStatus.PENDING, true);
        Reservation cancelled = reservation(101, 1, 7, LocalDate.now().minusDays(2),
                ReservationStatus.CANCELLED, true);
        when(reservationService.findReservations(isNull(), isNull(), isNull()))
                .thenReturn(List.of(live, cancelled));

        AssistantTools.ReservasVigentes result = tools.listarReservasVigentes(null, null);

        assertEquals(1, result.total());
        assertEquals(1, result.reservas().size());
        assertEquals("Cliente Uno", result.reservas().getFirst().cliente());
        assertEquals("Tomate", result.reservas().getFirst().semilla());
        assertEquals("Cama 1", result.reservas().getFirst().cama());
        assertEquals("PENDING", result.reservas().getFirst().estado());
    }

    @Test
    void listarLotesPorVencerSoloIncluyeDisponiblesDentroDelRango() {
        Seed tomate = seed(1, "Tomate", 500L);
        SeedLot near = lot(10, 1, LocalDate.now().plusDays(10), 200,
                SeedLotStatus.AVAILABLE);
        SeedLot far = lot(11, 1, LocalDate.now().plusDays(90), 200,
                SeedLotStatus.AVAILABLE);
        SeedLot expired = lot(12, 1, LocalDate.now().minusDays(1), 200,
                SeedLotStatus.AVAILABLE);
        SeedLot consumed = lot(13, 1, LocalDate.now().plusDays(5), 0,
                SeedLotStatus.DEPLETED);

        stubSnapshot(
                List.of(tomate),
                List.of(near, far, expired, consumed),
                List.of(),
                List.of(),
                List.of());

        AssistantTools.LotesPorVencer result = tools.listarLotesPorVencer(null);

        assertEquals(1, result.total());
        assertEquals(1, result.lotes().size());
        assertEquals(10, result.lotes().getFirst().loteId());
        assertEquals(10, result.lotes().getFirst().diasRestantes());
    }

    @Test
    void listarStockBajoUsaUmbralPorDefecto() {
        Seed tomate = seed(1, "Tomate", 50L);
        Seed lechuga = seed(2, "Lechuga", 150L);
        when(seedService.getAllSeeds()).thenReturn(List.of(tomate, lechuga));

        AssistantTools.StockBajo result = tools.listarStockBajo(null);

        assertEquals(100, result.umbral());
        assertEquals(1, result.total());
        assertEquals("Tomate", result.semillas().getFirst().semilla());
        assertEquals(50, result.semillas().getFirst().faltanteParaUmbral());
    }

    @Test
    void consultarInventarioSemillasFiltraPorNombreYExponeTipoYStock() {
        Seed papa = seed(5, "Papa Modificada", 800L);
        papa.setSeedType(new SeedTypeCatalog(3, "MODIFIED", "Semilla modificada"));
        Seed lechuga = seed(6, "Lechuga Tradicional", 350L);
        lechuga.setSeedType(new SeedTypeCatalog(2, "TRADITIONAL", "Semilla tradicional"));
        when(seedService.getAllSeeds()).thenReturn(List.of(papa, lechuga));

        AssistantTools.InventarioSemillas result =
                tools.consultarInventarioSemillas("semillas de lechuga hibrida");

        assertEquals(1, result.total());
        assertEquals("Lechuga Tradicional", result.semillas().getFirst().semilla());
        assertEquals("TRADITIONAL", result.semillas().getFirst().tipo());
        assertEquals(350, result.semillas().getFirst().stockSemillas());
        assertTrue(tracker.finish().contains("semillas"));
    }

    @Test
    void buscarClientesFiltraPorNombreYExponeRoles() {
        Contact raul = contact(8, "Raúl Pérez");
        Contact ana = contact(9, "Ana Torres");
        when(contactService.getAllContacts(ContactRole.CLIENT, false))
                .thenReturn(List.of(raul, ana));

        AssistantTools.ClientesEncontrados result = tools.buscarClientes("cliente raul");

        assertEquals(1, result.total());
        assertEquals("Raúl Pérez", result.clientes().getFirst().cliente());
        assertEquals("CLIENT", result.clientes().getFirst().roles());
        assertTrue(tracker.finish().contains("clientes"));
    }

    @Test
    void resumenVentasRechazaRangoInvertido() {
        assertThrows(IllegalArgumentException.class,
                () -> tools.resumenVentas(LocalDate.now(), LocalDate.now().minusDays(1)));
    }

    @Test
    void resumenOperativoCombinaSiembrasReservasVentasEInventario() {
        Seed tomate = seed(1, "Tomate", 50L);
        SeedLot tomateLot = lot(10, 1, LocalDate.now().plusDays(10), 200,
                SeedLotStatus.AVAILABLE);
        Sowing sowing = sowing(1, 10, 1, SowingStatus.IN_PROGRESS);
        Contact client = contact(7, "Cliente Uno");

        stubSnapshot(
                List.of(tomate),
                List.of(tomateLot),
                List.of(sowing),
                List.of(bed(1, "Cama 1")),
                List.of(client));
        when(saleService.getAvailabilityDetail(1))
                .thenReturn(new SaleService.Availability(1, 120, 20, 100));
        when(reservationService.findReservations(isNull(), isNull(), isNull()))
                .thenReturn(List.of(reservation(100, 1, 7, LocalDate.now(),
                        ReservationStatus.CONFIRMED, true)));
        when(saleService.findSales(isNull(), isNull(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(sale(1, 1, 30, "45000.00")));

        AssistantTools.ResumenOperativo result = tools.resumenOperativo();

        assertEquals(1, result.siembrasActivas());
        assertEquals(100, result.plantulasDisponibles());
        assertEquals(1, result.reservasVigentes());
        assertEquals(1, result.ventasUltimos30Dias());
        assertEquals(30, result.plantulasVendidasUltimos30Dias());
        assertEquals(new BigDecimal("45000.00"), result.totalVentasUltimos30Dias());
        assertEquals(1, result.semillasStockBajo());
        assertEquals(1, result.lotesPorVencer60Dias());
    }

    private void stubSnapshot(List<Seed> seeds,
                              List<SeedLot> lots,
                              List<Sowing> sowings,
                              List<Bed> beds,
                              List<Contact> contacts) {
        when(seedService.getAllSeeds()).thenReturn(seeds);
        when(inventoryService.getAllLots()).thenReturn(lots);
        when(sowingService.getAllSowings()).thenReturn(sowings);
        when(bedService.getAllBeds()).thenReturn(beds);
        when(contactService.getAllContacts(null, true)).thenReturn(contacts);
    }

    private Seed seed(int id, String name, Long totalAvailable) {
        Seed seed = new Seed();
        seed.setId(id);
        seed.setName(name);
        seed.setTotalAvailable(totalAvailable);
        seed.setActive(true);
        return seed;
    }

    private SeedLot lot(int id, int seedId, LocalDate dueDate, int available,
                        SeedLotStatus status) {
        SeedLot lot = new SeedLot();
        lot.setId(id);
        lot.setLotNumber(id);
        lot.setSeedId(seedId);
        lot.setDueDate(dueDate);
        lot.setAvailableQuantity(available);
        lot.setStatus(status);
        return lot;
    }

    private Sowing sowing(int id, int lotId, int bedId, SowingStatus status) {
        Sowing sowing = new Sowing();
        sowing.setId(id);
        sowing.setLotId(lotId);
        sowing.setBedId(bedId);
        sowing.setStatus(status);
        sowing.setActive(true);
        sowing.setSowingDate(LocalDate.now().minusDays(20));
        sowing.setQuantitySown(200);
        return sowing;
    }

    private Bed bed(int id, String code) {
        return new Bed(id, code, 1000, true);
    }

    private Contact contact(int id, String name) {
        Contact contact = new Contact();
        contact.setId(id);
        contact.setName(name);
        contact.setActive(true);
        contact.setRoles(Set.of(ContactRole.CLIENT));
        return contact;
    }

    private Reservation reservation(int id, int sowingId, int clientId, LocalDate date,
                                    ReservationStatus status, boolean active) {
        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setSowingId(sowingId);
        reservation.setClientId(clientId);
        reservation.setReservationDate(date);
        reservation.setReservationStatus(status);
        reservation.setTotalPrice(new BigDecimal("120000.00"));
        reservation.setActive(active);
        return reservation;
    }

    private Sale sale(int id, int sowingId, int quantity, String total) {
        Sale sale = new Sale();
        sale.setId(id);
        sale.setSowingId(sowingId);
        sale.setQuantity(quantity);
        sale.setTotal(new BigDecimal(total));
        sale.setActive(true);
        return sale;
    }
}
