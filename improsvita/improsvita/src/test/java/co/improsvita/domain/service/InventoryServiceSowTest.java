package co.improsvita.domain.service;

import co.improsvita.domain.model.Bed;
import co.improsvita.domain.model.SeedLot;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceSowTest {

    @Mock private SeedRepository seedRepository;
    @Mock private SupplierRepository supplierRepository;
    @Mock private LocationRepository locationRepository;
    @Mock private SeedLotRepository seedLotRepository;
    @Mock private SeedMovementRepository seedMovementRepository;
    @Mock private SeedSupplierRepository seedSupplierRepository;
    @Mock private SowingRepository sowingRepository;
    @Mock private BedRepository bedRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private SeedLot lot;
    private Bed bed;

    @BeforeEach
    void setUp() {
        lot = new SeedLot();
        lot.setId(1);
        lot.setAvailableQuantity(1000);

        bed = new Bed();
        bed.setId(2);
        bed.setCode("C-01");
        bed.setActive(true);
    }

    private void stubHappyPath() {
        when(seedLotRepository.getById(1)).thenReturn(lot);
        when(bedRepository.getById(2)).thenReturn(bed);
        when(seedLotRepository.save(any(SeedLot.class))).thenAnswer(i -> i.getArgument(0));
        when(sowingRepository.save(any(Sowing.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void sow_sinEstado_usaInProgressPorDefecto() {
        stubHappyPath();

        Sowing result = inventoryService.sow(1, 2, 100, LocalDate.now(), null, null, null);

        assertEquals(SowingStatus.IN_PROGRESS, result.getStatus());
    }

    @Test
    void sow_conEstadoPlanned_guardaEseEstado() {
        stubHappyPath();

        Sowing result = inventoryService.sow(1, 2, 100, LocalDate.now(), null, null, SowingStatus.PLANNED);

        assertEquals(SowingStatus.PLANNED, result.getStatus());
    }

    @Test
    void sow_conEstadoTerminal_lanzaErrorYNoToca​ElStock() {
        assertThrows(IllegalArgumentException.class, () ->
                inventoryService.sow(1, 2, 100, LocalDate.now(), null, null, SowingStatus.COMPLETED));

        verifyNoInteractions(seedLotRepository, sowingRepository, seedMovementRepository);
    }

    @Test
    void sow_descuentaStockDelLote() {
        stubHappyPath();

        inventoryService.sow(1, 2, 100, LocalDate.now(), null, null, null);

        assertEquals(900, lot.getAvailableQuantity());
    }
}