package co.improsvita.web.controller;

import co.improsvita.domain.model.Sowing;
import co.improsvita.domain.model.SowingStatus;
import co.improsvita.domain.service.InventoryService;
import co.improsvita.domain.service.SaleService;
import co.improsvita.domain.service.SowingService;
import co.improsvita.domain.service.VisitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SowingControllerTest {

    @Mock
    private InventoryService inventoryService;

    @Mock
    private SowingService sowingService;

    @Mock
    private VisitService visitService;

    @Mock
    private SaleService saleService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new SowingController(
                        inventoryService, sowingService, visitService, saleService))
                .build();
    }

    private Sowing sowing() {
        Sowing sowing = new Sowing();
        sowing.setId(3);
        sowing.setLotId(7);
        sowing.setBedId(1);
        sowing.setQuantitySown(150);
        sowing.setEstimatedSeedlings(127);
        sowing.setSowingDate(LocalDate.of(2026, 10, 2));
        sowing.setStatus(SowingStatus.IN_PROGRESS);
        sowing.setActive(true);
        return sowing;
    }

    @Test
    void getSowings_exponeGerminadasVendidasYDisponiblesNetas() throws Exception {
        when(sowingService.getAllSowings()).thenReturn(List.of(sowing()));
        when(visitService.getLatestAvailableSeedlings(3)).thenReturn(120);
        when(saleService.getSoldQuantity(3)).thenReturn(40);

        mockMvc.perform(get("/sowings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estimatedSeedlings").value(127))
                .andExpect(jsonPath("$[0].germinatedQuantity").value(120))
                .andExpect(jsonPath("$[0].soldQuantity").value(40))
                .andExpect(jsonPath("$[0].seedlingsAvailable").value(80));
    }

    @Test
    void getSowingById_sinVisitas_disponibleCero() throws Exception {
        when(sowingService.getSowingById(3)).thenReturn(sowing());
        when(visitService.getLatestAvailableSeedlings(3)).thenReturn(null);
        when(saleService.getSoldQuantity(3)).thenReturn(0);

        mockMvc.perform(get("/sowings/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.germinatedQuantity").doesNotExist())
                .andExpect(jsonPath("$.soldQuantity").value(0))
                .andExpect(jsonPath("$.seedlingsAvailable").value(0));
    }
}
