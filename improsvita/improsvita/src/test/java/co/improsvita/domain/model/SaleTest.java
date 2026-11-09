package co.improsvita.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaleTest {

    private Sale sale(Integer quantity, String unitPrice) {
        Sale sale = new Sale();
        sale.setQuantity(quantity);
        sale.setUnitPrice(unitPrice != null ? new BigDecimal(unitPrice) : null);
        sale.setActive(true);
        return sale;
    }

    @Test
    void expectedTotal_multiplicaCantidadPorPrecio() {
        assertEquals(0, new BigDecimal("37500.00").compareTo(sale(3, "12500.00").expectedTotal()));
    }

    @Test
    void expectedTotal_sinDatos_devuelveNull() {
        assertNull(sale(null, "12500.00").expectedTotal());
        assertNull(sale(3, null).expectedTotal());
    }

    @Test
    void sinActive_laVentaEstaActivaPorDefecto() {
        Sale nueva = new Sale();
        nueva.setActive(true);

        assertFalse(nueva.isCancelled());
        assertTrue(nueva.getActive());
    }

    @Test
    void activeFalse_marcaLaVentaComoAnulada() {
        Sale anulada = sale(3, "100.00");
        anulada.setActive(false);

        assertTrue(anulada.isCancelled());
    }

    @Test
    void billsReservation_soloConReservaYVentaActiva() {
        Sale directa = sale(1, "10.00");
        assertFalse(directa.billsReservation());

        Sale deReserva = sale(1, "10.00");
        deReserva.setReservationId(7);
        assertTrue(deReserva.billsReservation());

        deReserva.setActive(false);
        assertFalse(deReserva.billsReservation());
    }
}
