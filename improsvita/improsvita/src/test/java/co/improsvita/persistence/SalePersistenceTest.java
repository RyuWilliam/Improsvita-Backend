package co.improsvita.persistence;

import co.improsvita.persistence.crud.ContactJpaRepository;
import co.improsvita.persistence.crud.ReservationJpaRepository;
import co.improsvita.persistence.crud.SaleJpaRepository;
import co.improsvita.persistence.crud.SowingJpaRepository;
import co.improsvita.persistence.entities.ContactEntity;
import co.improsvita.persistence.entities.ReservationEntity;
import co.improsvita.persistence.entities.SaleEntity;
import co.improsvita.persistence.entities.SowingEntity;
import co.improsvita.persistence.enums.SaleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica que el mapeo JPA de {@link SaleEntity} coincide con la tabla
 * {@code sales}: columnas, precisión monetaria, enums, auditoría y checks.
 */
@SpringBootTest
@Transactional
class SalePersistenceTest {

    @Autowired
    private SaleJpaRepository saleJpaRepository;

    @Autowired
    private ContactJpaRepository contactJpaRepository;

    @Autowired
    private SowingJpaRepository sowingJpaRepository;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    private ContactEntity buyer;
    private SowingEntity sowing;

    @BeforeEach
    void setUp() {
        buyer = contactJpaRepository.findById(3).orElseThrow();
        sowing = sowingJpaRepository.findById(3).orElseThrow();
    }

    private SaleEntity newSale(int quantity, String unitPrice, String total) {
        SaleEntity sale = new SaleEntity();
        sale.setContact(buyer);
        sale.setSowing(sowing);
        sale.setSaleDate(LocalDate.now());
        sale.setQuantity(quantity);
        sale.setUnitPrice(new BigDecimal(unitPrice));
        sale.setTotal(new BigDecimal(total));
        sale.setSaleType(SaleType.DIRECT);
        sale.setActive(true);
        return sale;
    }

    @Test
    void guardaVentaDirecta_conAuditoriaYPrecisionMonetaria() {
        SaleEntity saved = saleJpaRepository.saveAndFlush(newSale(10, "1250.50", "12505.00"));

        assertTrue(saved.getSaleId() != null);
        assertTrue(saved.getCreatedDate() != null);
        assertTrue(saved.getLastUpdated() != null);
        assertEquals(0, new BigDecimal("12505.00").compareTo(saved.getTotal()));
        assertEquals(SaleType.DIRECT, saved.getSaleType());
        assertNull(saved.getReservation());
    }

    @Test
    void guardaVentaDeReserva_conReservaAsociada() {
        ReservationEntity reservation = reservationJpaRepository.findById(1).orElseThrow();
        SaleEntity sale = newSale(5, "100.00", "500.00");
        sale.setSaleType(SaleType.RESERVATION);
        sale.setReservation(reservation);

        SaleEntity saved = saleJpaRepository.saveAndFlush(sale);

        assertEquals(reservation.getReservationId(),
                saleJpaRepository.findById(saved.getSaleId()).orElseThrow()
                        .getReservation().getReservationId());
    }

    @Test
    void rechazaDosVentasParaLaMismaReserva() {
        ReservationEntity reservation = reservationJpaRepository.findById(1).orElseThrow();
        SaleEntity first = newSale(5, "100.00", "500.00");
        first.setSaleType(SaleType.RESERVATION);
        first.setReservation(reservation);
        saleJpaRepository.saveAndFlush(first);

        SaleEntity second = newSale(5, "100.00", "500.00");
        second.setSaleType(SaleType.RESERVATION);
        second.setReservation(reservation);

        assertThrows(DataIntegrityViolationException.class,
                () -> saleJpaRepository.saveAndFlush(second));
    }

    @Test
    void rechazaCantidadNoPositiva() {
        assertThrows(DataIntegrityViolationException.class,
                () -> saleJpaRepository.saveAndFlush(newSale(0, "100.00", "0.00")));
    }

    @Test
    void sumaSoloVentasActivasDeLaSiembra() {
        SaleEntity activa = saleJpaRepository.saveAndFlush(newSale(10, "100.00", "1000.00"));
        SaleEntity anulada = newSale(7, "100.00", "700.00");
        anulada.setActive(false);
        saleJpaRepository.saveAndFlush(anulada);

        assertEquals(10, saleJpaRepository.sumActiveQuantityBySowing(sowing.getSowingId()));
        assertTrue(saleJpaRepository.existsBySowing_SowingIdAndActiveTrue(sowing.getSowingId()));
        assertTrue(saleJpaRepository.findByReservation_ReservationId(1).isEmpty());
        assertTrue(saleJpaRepository.findById(activa.getSaleId()).isPresent());
    }
}
