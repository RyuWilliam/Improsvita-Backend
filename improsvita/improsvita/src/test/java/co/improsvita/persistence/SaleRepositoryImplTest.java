package co.improsvita.persistence;

import co.improsvita.domain.model.Sale;
import co.improsvita.domain.model.SaleType;
import co.improsvita.domain.model.PageResult;
import co.improsvita.domain.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica el adaptador {@link SaleRepositoryImpl}: mapeo dominio ↔ entidad,
 * filtros del historial y suma de cantidad vendida.
 */
@SpringBootTest
@Transactional
class SaleRepositoryImplTest {

    private static final int BUYER_ID = 3;
    private static final int OTHER_BUYER_ID = 4;
    private static final int SOWING_ID = 3;

    @Autowired
    private SaleRepository saleRepository;

    private Sale newSale(int quantity, String unitPrice, SaleType type, int contactId, LocalDate date) {
        Sale sale = new Sale();
        sale.setContactId(contactId);
        sale.setSowingId(SOWING_ID);
        sale.setSaleDate(date);
        sale.setQuantity(quantity);
        sale.setUnitPrice(new BigDecimal(unitPrice));
        sale.setTotal(new BigDecimal(unitPrice).multiply(BigDecimal.valueOf(quantity)));
        sale.setSaleType(type);
        sale.setActive(true);
        return sale;
    }

    @BeforeEach
    void clean() {
        saleRepository.getAll(null, null, null, null)
                .forEach(sale -> saleRepository.deleteById(sale.getId()));
    }

    @Test
    void guardaYLee_mapeandoTodosLosCampos() {
        Sale saved = saleRepository.save(
                newSale(10, "1250.50", SaleType.DIRECT, BUYER_ID, LocalDate.now()));

        assertNotNull(saved.getId());
        Sale found = saleRepository.getById(saved.getId());

        assertEquals(BUYER_ID, found.getContactId());
        assertEquals(SOWING_ID, found.getSowingId());
        assertEquals(10, found.getQuantity());
        assertEquals(SaleType.DIRECT, found.getSaleType());
        assertNull(found.getReservationId());
        assertEquals(0, new BigDecimal("12505.00").compareTo(found.getTotal()));
    }

    @Test
    void getAll_filtraPorContactoTipoYRangoDeFechas() {
        LocalDate old = LocalDate.of(2026, 1, 10);
        LocalDate mid = LocalDate.of(2026, 1, 15);
        LocalDate recent = LocalDate.of(2026, 1, 20);

        saleRepository.save(newSale(5, "100.00", SaleType.DIRECT, BUYER_ID, old));
        saleRepository.save(newSale(6, "100.00", SaleType.DIRECT, OTHER_BUYER_ID, mid));
        saleRepository.save(newSale(7, "100.00", SaleType.RESERVATION, BUYER_ID, recent));

        // sin filtros
        assertEquals(3, saleRepository.getAll(null, null, null, null).size());
        // por contacto
        assertEquals(2, saleRepository.getAll(BUYER_ID, null, null, null).size());
        // por tipo
        assertEquals(2, saleRepository.getAll(null, SaleType.DIRECT, null, null).size());
        // contacto + tipo
        assertEquals(1, saleRepository.getAll(BUYER_ID, SaleType.RESERVATION, null, null).size());
        // desde el 15 inclusive -> 2 (mid y recent)
        assertEquals(2, saleRepository.getAll(null, null, mid, null).size());
        // hasta el 15 inclusive -> 2 (old y mid)
        assertEquals(2, saleRepository.getAll(null, null, null, mid).size());
        // rango cerrado que solo contiene mid
        assertEquals(1, saleRepository.getAll(null, null, mid, mid).size());
    }

    @Test
    void getAll_ordenadasDeMasRecienteAMasAntigua() {
        saleRepository.save(newSale(1, "100.00", SaleType.DIRECT, BUYER_ID,
                LocalDate.now().minusDays(10)));
        saleRepository.save(newSale(2, "100.00", SaleType.DIRECT, BUYER_ID,
                LocalDate.now().minusDays(1)));

        List<Sale> sales = saleRepository.getAll(BUYER_ID, null, null, null);

        assertEquals(2, sales.get(0).getQuantity());
        assertEquals(1, sales.get(1).getQuantity());
    }

    @Test
    void getSoldQuantity_sumaSoloVentasActivas() {
        saleRepository.save(newSale(10, "100.00", SaleType.DIRECT, BUYER_ID, LocalDate.now()));
        Sale anulada = saleRepository.save(
                newSale(7, "100.00", SaleType.DIRECT, BUYER_ID, LocalDate.now()));
        anulada.setActive(false);
        saleRepository.save(anulada);

        assertEquals(10, saleRepository.getSoldQuantity(SOWING_ID));
    }

    @Test
    void getSoldQuantity_sinVentas_devuelveCero() {
        assertEquals(0, saleRepository.getSoldQuantity(SOWING_ID));
    }

    @Test
    void getByReservationId_ignoraVentasAnuladas() {
        Sale sale = newSale(5, "100.00", SaleType.RESERVATION, BUYER_ID, LocalDate.now());
        sale.setReservationId(1);
        Sale saved = saleRepository.save(sale);

        assertEquals(saved.getId(), saleRepository.getByReservationId(1).getId());

        saved.setActive(false);
        saleRepository.save(saved);

        assertNull(saleRepository.getByReservationId(1));
    }

    @Test
    void getBySowingId_devuelveLasVentasDeLaSiembra() {
        saleRepository.save(newSale(5, "100.00", SaleType.DIRECT, BUYER_ID, LocalDate.now()));

        List<Sale> sales = saleRepository.getBySowingId(SOWING_ID);

        assertEquals(1, sales.size());
        assertEquals(SOWING_ID, sales.get(0).getSowingId());
        assertTrue(sales.stream().allMatch(s -> s.getActive()));
    }

    @Test
    void getPage_paginaYCuentaElTotal() {
        for (int i = 1; i <= 5; i++) {
            saleRepository.save(newSale(i, "100.00", SaleType.DIRECT, BUYER_ID,
                    LocalDate.of(2026, 1, i)));
        }

        PageResult<Sale> first = saleRepository.getPage(null, null, null, null, 0, 2);
        PageResult<Sale> last = saleRepository.getPage(null, null, null, null, 2, 2);

        assertEquals(2, first.getContent().size());
        assertEquals(5, first.getTotalElements());
        assertEquals(3, first.getTotalPages());
        assertEquals(0, first.getPage());
        assertTrue(first.hasNext());
        assertFalse(first.hasPrevious());

        assertEquals(1, last.getContent().size());
        assertFalse(last.hasNext());
        assertTrue(last.hasPrevious());
    }

    @Test
    void getPage_ordenaDeMasRecienteAMasAntigua() {
        for (int i = 1; i <= 3; i++) {
            saleRepository.save(newSale(i, "100.00", SaleType.DIRECT, BUYER_ID,
                    LocalDate.of(2026, 1, i)));
        }

        PageResult<Sale> page = saleRepository.getPage(null, null, null, null, 0, 10);

        assertEquals(3, page.getContent().get(0).getQuantity());
        assertEquals(1, page.getContent().get(2).getQuantity());
    }

    @Test
    void getPage_respetaLosFiltros() {
        saleRepository.save(newSale(5, "100.00", SaleType.DIRECT, BUYER_ID,
                LocalDate.of(2026, 1, 10)));
        saleRepository.save(newSale(6, "100.00", SaleType.DIRECT, OTHER_BUYER_ID,
                LocalDate.of(2026, 1, 15)));
        saleRepository.save(newSale(7, "100.00", SaleType.RESERVATION, BUYER_ID,
                LocalDate.of(2026, 1, 20)));

        PageResult<Sale> byBuyer = saleRepository.getPage(BUYER_ID, null, null, null, 0, 10);
        PageResult<Sale> byType = saleRepository.getPage(null, SaleType.DIRECT, null, null, 0, 10);
        PageResult<Sale> byRange = saleRepository.getPage(null, null,
                LocalDate.of(2026, 1, 12), LocalDate.of(2026, 1, 18), 0, 10);

        assertEquals(2, byBuyer.getTotalElements());
        assertEquals(2, byType.getTotalElements());
        assertEquals(1, byRange.getTotalElements());
    }

    @Test
    void getPage_sinResultados_devuelvePaginaVacia() {
        PageResult<Sale> page = saleRepository.getPage(null, null, null, null, 0, 10);

        assertTrue(page.getContent().isEmpty());
        assertEquals(0, page.getTotalElements());
        assertEquals(0, page.getTotalPages());
        assertFalse(page.hasNext());
    }
}
