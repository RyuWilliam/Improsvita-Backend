package co.improsvita.domain.service;

import co.improsvita.domain.exception.InvalidDateRangeException;
import co.improsvita.domain.model.Bed;
import co.improsvita.domain.model.MovementType;
import co.improsvita.domain.model.SeedLot;
import co.improsvita.domain.model.SeedLotStatus;
import co.improsvita.domain.model.SeedMovement;
import co.improsvita.domain.model.Sowing;
import co.improsvita.domain.model.SowingStatus;
import co.improsvita.domain.repository.BedRepository;
import co.improsvita.domain.repository.LocationRepository;
import co.improsvita.domain.repository.SeedLotRepository;
import co.improsvita.domain.repository.SeedMovementRepository;
import co.improsvita.domain.repository.SeedRepository;
import co.improsvita.domain.repository.SeedSupplierRepository;
import co.improsvita.domain.repository.SowingRepository;
import co.improsvita.domain.repository.SupplierRepository;
import co.improsvita.domain.validation.DateRangeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de las reglas de inventario y siembra (RF 2.1):
 * descuento de stock, registro automático de movimientos y validación de fechas.
 */
@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock private SeedRepository seedRepository;
    @Mock private SupplierRepository supplierRepository;
    @Mock private LocationRepository locationRepository;
    @Mock private SeedLotRepository seedLotRepository;
    @Mock private SeedMovementRepository seedMovementRepository;
    @Mock private SeedSupplierRepository seedSupplierRepository;
    @Mock private SowingRepository sowingRepository;
    @Mock private BedRepository bedRepository;

    private InventoryService service;

    @BeforeEach
    void setUp() {
        service = new InventoryService(seedRepository, supplierRepository, locationRepository,
                seedLotRepository, seedMovementRepository, seedSupplierRepository,
                sowingRepository, bedRepository, new DateRangeValidator());
    }

    // ---------- helpers ----------

    private SeedLot lot(String available) {
        SeedLot lot = new SeedLot();
        lot.setId(1);
        lot.setLotNumber(100);
        lot.setAvailableQuantity(new BigDecimal(available));
        lot.setInitialQuantity(new BigDecimal(available));
        lot.setStatus(SeedLotStatus.AVAILABLE);
        return lot;
    }

    private Bed bed(boolean active) {
        Bed bed = new Bed();
        bed.setId(7);
        bed.setCode("A-01");
        bed.setActive(active);
        return bed;
    }

    private void stubSaves() {
        when(seedLotRepository.save(any(SeedLot.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private void stubSowHappyPath(SeedLot lot) {
        when(seedLotRepository.getById(1)).thenReturn(lot);
        when(bedRepository.getById(7)).thenReturn(bed(true));
        stubSaves();
        when(sowingRepository.save(any(Sowing.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private SeedMovement capturedMovement() {
        ArgumentCaptor<SeedMovement> captor = ArgumentCaptor.forClass(SeedMovement.class);
        verify(seedMovementRepository).save(captor.capture());
        return captor.getValue();
    }

    private void assertAmount(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                "esperado " + expected + " pero fue " + actual);
    }

    // ---------- salidas ----------

    @Test
    void registerExit_discountsStockAndRecordsExitMovement() {
        when(seedLotRepository.getById(1)).thenReturn(lot("100"));
        stubSaves();

        SeedLot result = service.registerExit(1, new BigDecimal("30"), "Venta");

        assertAmount("70", result.getAvailableQuantity());
        assertEquals(SeedLotStatus.AVAILABLE, result.getStatus());
        SeedMovement movement = capturedMovement();
        assertEquals(MovementType.EXIT, movement.getMovementType());
        assertEquals(1, movement.getLotId());
        assertAmount("30", movement.getQuantity());
        assertEquals("Venta", movement.getReason());
        assertNotNull(movement.getMovementDate());
    }

    @Test
    void registerExit_wholeStock_marksLotDepleted() {
        when(seedLotRepository.getById(1)).thenReturn(lot("10"));
        stubSaves();

        SeedLot result = service.registerExit(1, new BigDecimal("10"), "Venta");

        assertAmount("0", result.getAvailableQuantity());
        assertEquals(SeedLotStatus.DEPLETED, result.getStatus());
    }

    @Test
    void registerExit_insufficientStock_throwsAndRecordsNothing() {
        when(seedLotRepository.getById(1)).thenReturn(lot("10"));

        assertThrows(IllegalStateException.class,
                () -> service.registerExit(1, new BigDecimal("20"), "Venta"));

        verify(seedMovementRepository, never()).save(any());
        verify(seedLotRepository, never()).save(any());
    }

    @Test
    void registerExit_zeroQuantity_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.registerExit(1, BigDecimal.ZERO, "Venta"));
        verifyNoInteractions(seedMovementRepository);
    }

    // ---------- ajustes ----------

    @Test
    void adjustStock_positive_addsStockAndRecordsAdjustment() {
        when(seedLotRepository.getById(1)).thenReturn(lot("10"));
        stubSaves();

        SeedLot result = service.adjustStock(1, new BigDecimal("5"), "Conteo físico");

        assertAmount("15", result.getAvailableQuantity());
        SeedMovement movement = capturedMovement();
        assertEquals(MovementType.ADJUSTMENT, movement.getMovementType());
        assertAmount("5", movement.getQuantity());
    }

    @Test
    void adjustStock_resultingNegative_throwsAndRecordsNothing() {
        when(seedLotRepository.getById(1)).thenReturn(lot("5"));

        assertThrows(IllegalStateException.class,
                () -> service.adjustStock(1, new BigDecimal("-10"), "Merma"));

        verify(seedMovementRepository, never()).save(any());
    }

    // ---------- siembra (RF 2.1) ----------

    @Test
    void sow_discountsLot_recordsExitMovement_andCreatesSowing() {
        SeedLot lot = lot("100");
        stubSowHappyPath(lot);

        Sowing sowing = service.sow(1, 7, new BigDecimal("40"),
                LocalDate.of(2026, 10, 6), LocalDate.of(2026, 10, 16), "Primera siembra");

        assertAmount("60", lot.getAvailableQuantity());
        assertEquals(SowingStatus.IN_PROGRESS, sowing.getStatus());
        assertAmount("40", sowing.getQuantitySown());
        assertEquals(7, sowing.getBedId());
        assertEquals(LocalDate.of(2026, 10, 6), sowing.getSowingDate());

        SeedMovement movement = capturedMovement();
        assertEquals(MovementType.EXIT, movement.getMovementType());
        assertEquals("Siembra en cama A-01", movement.getReason());
        assertAmount("40", movement.getQuantity());
    }

    @Test
    void sow_withoutExpectedGerminationDate_isAllowed() {
        stubSowHappyPath(lot("100"));

        Sowing sowing = service.sow(1, 7, new BigDecimal("10"), LocalDate.of(2026, 10, 6), null, null);

        assertNull(sowing.getExpectedGerminationDate());
    }

    @Test
    void sow_withoutSowingDate_usesToday() {
        stubSowHappyPath(lot("100"));

        Sowing sowing = service.sow(1, 7, new BigDecimal("10"), null, null, null);

        assertEquals(LocalDate.now(), sowing.getSowingDate());
    }

    @Test
    void sow_germinationBeforeSowingDate_throwsInvalidDateRange_andTouchesNothing() {
        assertThrows(InvalidDateRangeException.class,
                () -> service.sow(1, 7, new BigDecimal("10"),
                        LocalDate.of(2026, 10, 10), LocalDate.of(2026, 10, 1), null));

        verifyNoInteractions(seedLotRepository, seedMovementRepository, sowingRepository);
    }

    @Test
    void sow_insufficientStock_throwsAndRecordsNothing() {
        when(seedLotRepository.getById(1)).thenReturn(lot("5"));
        when(bedRepository.getById(7)).thenReturn(bed(true));

        assertThrows(IllegalStateException.class,
                () -> service.sow(1, 7, new BigDecimal("10"), LocalDate.of(2026, 10, 6), null, null));

        verify(seedMovementRepository, never()).save(any());
        verify(sowingRepository, never()).save(any());
    }

    @Test
    void sow_inactiveBed_throwsAndRecordsNothing() {
        when(seedLotRepository.getById(1)).thenReturn(lot("100"));
        when(bedRepository.getById(7)).thenReturn(bed(false));

        assertThrows(IllegalStateException.class,
                () -> service.sow(1, 7, new BigDecimal("10"), LocalDate.of(2026, 10, 6), null, null));

        verify(seedMovementRepository, never()).save(any());
    }
}
