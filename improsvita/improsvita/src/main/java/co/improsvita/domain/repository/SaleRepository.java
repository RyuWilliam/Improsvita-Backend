package co.improsvita.domain.repository;

import co.improsvita.domain.model.PageResult;
import co.improsvita.domain.model.Sale;
import co.improsvita.domain.model.SaleType;

import java.time.LocalDate;
import java.util.List;

public interface SaleRepository {

    /** Historial con filtros opcionales (todos pueden ser null). */
    List<Sale> getAll(Integer contactId, SaleType type, LocalDate from, LocalDate to);

    /** Historial paginado con los mismos filtros opcionales. */
    PageResult<Sale> getPage(Integer contactId, SaleType type, LocalDate from, LocalDate to,
                             int page, int size);

    List<Sale> getBySowingId(Integer sowingId);

    Sale getById(Integer id);

    /** Plántulas ya vendidas (solo ventas activas) de una siembra. */
    Integer getSoldQuantity(Integer sowingId);

    /** Venta activa asociada a una reserva, o null si aún no se facturó. */
    Sale getByReservationId(Integer reservationId);

    /** Venta asociada a una reserva, incluso si fue anulada. */
    Sale getByReservationIdIncludingInactive(Integer reservationId);

    Sale save(Sale sale);

    void deleteById(Integer id);
}
