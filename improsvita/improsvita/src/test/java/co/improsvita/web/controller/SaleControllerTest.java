package co.improsvita.web.controller;

import co.improsvita.domain.model.PageResult;
import co.improsvita.domain.model.Sale;
import co.improsvita.domain.model.SaleType;
import co.improsvita.domain.service.SaleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SaleControllerTest {

    @Mock
    private SaleService saleService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new SaleController(saleService))
                .build();
    }

    private Sale sale(Integer id) {
        Sale sale = new Sale();
        sale.setId(id);
        sale.setContactId(3);
        sale.setSowingId(3);
        sale.setSaleDate(LocalDate.of(2026, 1, 15));
        sale.setQuantity(10);
        sale.setUnitPrice(new BigDecimal("1250.50"));
        sale.setTotal(new BigDecimal("12505.00"));
        sale.setSaleType(SaleType.DIRECT);
        sale.setActive(true);
        return sale;
    }

    // ------------------------------------------------------------ venta directa

    @Test
    void post_ventaDirecta_responde201() throws Exception {
        when(saleService.registerDirectSale(eq(3), eq(3), eq(10), any(BigDecimal.class),
                any(), isNull())).thenReturn(sale(1));

        mockMvc.perform(post("/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"contactId":3,"sowingId":3,"quantity":10,"unitPrice":1250.50}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.saleType").value("DIRECT"))
                .andExpect(jsonPath("$.total").value(12505.00));
    }

    @Test
    void post_sinCamposObligatorios_responde400() throws Exception {
        mockMvc.perform(post("/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":10}"))
                .andExpect(status().isBadRequest());

        verify(saleService, never()).registerDirectSale(any(), any(), any(), any(), any(), any());
    }

    @Test
    void post_cantidadCero_responde400() throws Exception {
        mockMvc.perform(post("/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"contactId":3,"sowingId":3,"quantity":0,"unitPrice":100}
                                """))
                .andExpect(status().isBadRequest());

        verify(saleService, never()).registerDirectSale(any(), any(), any(), any(), any(), any());
    }

    @Test
    void post_datosInvalidos_responde400ConMensaje() throws Exception {
        when(saleService.registerDirectSale(any(), any(), any(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("La cantidad debe ser mayor que cero"));

        mockMvc.perform(post("/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"contactId":3,"sowingId":3,"quantity":5,"unitPrice":100}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("La cantidad debe ser mayor que cero"));
    }

    @Test
    void post_reglaIncumplida_responde409() throws Exception {
        when(saleService.registerDirectSale(any(), any(), any(), any(), any(), any()))
                .thenThrow(new IllegalStateException("La siembra tiene una reserva vigente"));

        mockMvc.perform(post("/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"contactId":3,"sowingId":3,"quantity":5,"unitPrice":100}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").value("La siembra tiene una reserva vigente"));
    }

    // ------------------------------------------------------------ venta de reserva

    @Test
    void post_reserva_responde201() throws Exception {
        Sale sale = sale(2);
        sale.setSaleType(SaleType.RESERVATION);
        sale.setReservationId(7);
        when(saleService.registerReservationSale(eq(7), eq(10), any(BigDecimal.class),
                any(), isNull())).thenReturn(sale);

        mockMvc.perform(post("/sales/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reservationId":7,"quantity":10,"unitPrice":1250.50}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.saleType").value("RESERVATION"))
                .andExpect(jsonPath("$.reservationId").value(7));
    }

    @Test
    void post_reservaYaFacturada_responde409() throws Exception {
        when(saleService.registerReservationSale(any(), any(), any(), any(), any()))
                .thenThrow(new IllegalStateException("La reserva 7 ya fue facturada"));

        mockMvc.perform(post("/sales/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reservationId":7,"quantity":10,"unitPrice":100}
                                """))
                .andExpect(status().isConflict());
    }

    // ------------------------------------------------------------ consultas

    @Test
    void get_listadoSinFiltros_devuelveTodas() throws Exception {
        when(saleService.findSales(null, null, null, null)).thenReturn(List.of(sale(1), sale(2)));

        mockMvc.perform(get("/sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void get_filtradoPorClienteTipoYFechas_propagaParametros() throws Exception {
        when(saleService.findSales(eq(3), eq(SaleType.DIRECT),
                eq(LocalDate.of(2026, 1, 1)), eq(LocalDate.of(2026, 1, 31))))
                .thenReturn(List.of(sale(1)));

        mockMvc.perform(get("/sales")
                        .param("contactId", "3")
                        .param("type", "DIRECT")
                        .param("from", "2026-01-01")
                        .param("to", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(saleService).findSales(3, SaleType.DIRECT,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));
    }

    @Test
    void get_tipoInvalido_responde400() throws Exception {
        mockMvc.perform(get("/sales").param("type", "OTRA"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_rangoInvertido_responde400() throws Exception {
        when(saleService.findSales(any(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("La fecha inicial no puede ser posterior a la final"));

        mockMvc.perform(get("/sales")
                        .param("from", "2026-02-01")
                        .param("to", "2026-01-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_porId_existente() throws Exception {
        when(saleService.getSaleById(1)).thenReturn(sale(1));

        mockMvc.perform(get("/sales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    void get_porId_inexistente_responde404() throws Exception {
        when(saleService.getSaleById(99)).thenReturn(null);

        mockMvc.perform(get("/sales/99")).andExpect(status().isNotFound());
    }

    // ------------------------------------------------------------ paginacion

    @Test
    void get_page_devuelveEstructuraPaginada() throws Exception {
        PageResult<Sale> page = new PageResult<>(List.of(sale(1), sale(2)), 0, 2, 5, 3);
        when(saleService.findSalesPage(null, null, null, null, 0, 2)).thenReturn(page);

        mockMvc.perform(get("/sales/page").param("page", "0").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.hasPrevious").value(false));
    }

    @Test
    void get_page_conFiltros_losPropaga() throws Exception {
        PageResult<Sale> page = new PageResult<>(List.of(sale(1)), 1, 10, 11, 2);
        when(saleService.findSalesPage(eq(3), eq(SaleType.RESERVATION),
                eq(LocalDate.of(2026, 1, 1)), eq(LocalDate.of(2026, 1, 31)), eq(1), eq(10)))
                .thenReturn(page);

        mockMvc.perform(get("/sales/page")
                        .param("contactId", "3")
                        .param("type", "RESERVATION")
                        .param("from", "2026-01-01")
                        .param("to", "2026-01-31")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasPrevious").value(true));
    }

    @Test
    void get_page_usaTamanoPorDefecto() throws Exception {
        when(saleService.findSalesPage(null, null, null, null, 0, 20))
                .thenReturn(new PageResult<>(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/sales/page"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(20));

        verify(saleService).findSalesPage(null, null, null, null, 0, 20);
    }

    @Test
    void get_page_tamanoExcesivo_responde400() throws Exception {
        when(saleService.findSalesPage(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenThrow(new IllegalArgumentException("El tamaño de página no puede superar 100"));

        mockMvc.perform(get("/sales/page").param("size", "500"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_page_paginaNegativa_responde400() throws Exception {
        when(saleService.findSalesPage(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenThrow(new IllegalArgumentException("El número de página no puede ser negativo"));

        mockMvc.perform(get("/sales/page").param("page", "-1"))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------ disponibilidad

    @Test
    void get_disponibilidad_devuelveDesglose() throws Exception {
        when(saleService.getAvailabilityDetail(3))
                .thenReturn(new SaleService.Availability(3, 120, 30, 90));

        mockMvc.perform(get("/sales/availability/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sowingId").value(3))
                .andExpect(jsonPath("$.seedlingsAvailable").value(120))
                .andExpect(jsonPath("$.soldQuantity").value(30))
                .andExpect(jsonPath("$.available").value(90));
    }

    @Test
    void get_disponibilidad_siembraInexistente_responde404() throws Exception {
        when(saleService.getAvailabilityDetail(99))
                .thenThrow(new IllegalArgumentException("Siembra no encontrada: 99"));

        mockMvc.perform(get("/sales/availability/99")).andExpect(status().isNotFound());
    }

    // ------------------------------------------------------------ anulacion

    @Test
    void delete_anulaLaVenta_responde204() throws Exception {
        mockMvc.perform(delete("/sales/1")).andExpect(status().isNoContent());

        verify(saleService).cancelSale(1);
    }

    @Test
    void delete_inexistente_responde404() throws Exception {
        when(saleService.cancelSale(99))
                .thenThrow(new IllegalArgumentException("Venta no encontrada: 99"));

        mockMvc.perform(delete("/sales/99")).andExpect(status().isNotFound());
    }

    @Test
    void delete_yaAnulada_responde409() throws Exception {
        when(saleService.cancelSale(1))
                .thenThrow(new IllegalStateException("La venta 1 ya está anulada"));

        mockMvc.perform(delete("/sales/1")).andExpect(status().isConflict());
    }
}
