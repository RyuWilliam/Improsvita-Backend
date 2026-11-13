package co.improsvita.domain.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Ventas de plántulas.
 * <p>
 * Reglas:
 * <ul>
 *   <li>El comprador debe existir y tener el rol CLIENT.</li>
 *   <li>La siembra debe existir, estar activa y en PLANNED/IN_PROGRESS/COMPLETED.</li>
 *   <li>Una siembra con reserva viva no se vende directo: esa producción está comprometida.</li>
 *   <li>La cantidad no puede superar las plántulas disponibles (última visita activa
 *       menos lo ya vendido).</li>
 *   <li>El total lo calcula el servidor: {@code quantity * unitPrice}.</li>
 *   <li>Una reserva se factura como máximo una vez.</li>
 * </ul>
 */
@Service
public class SaleService {

    /** Se puede vender producción en curso o ya completada. */
    private static final Set<SowingStatus> SELLABLE = Set.of(
            SowingStatus.PLANNED, SowingStatus.IN_PROGRESS, SowingStatus.COMPLETED);

    private static final int MAX_NOTES_LENGTH = 500;

    /** Tope de resultados por página en el historial. */
    private static final int MAX_PAGE_SIZE = 100;

    private final SaleRepository saleRepository;
    private final SowingRepository sowingRepository;
    private final ContactService contactService;
    private final VisitService visitService;
    private final ReservationService reservationService;

    public SaleService(SaleRepository saleRepository,
                       SowingRepository sowingRepository,
                       ContactService contactService,
                       VisitService visitService,
                       ReservationService reservationService) {
        this.saleRepository = saleRepository;
        this.sowingRepository = sowingRepository;
        this.contactService = contactService;
        this.visitService = visitService;
        this.reservationService = reservationService;
    }

    public List<Sale> findSales(Integer contactId, SaleType type, LocalDate from, LocalDate to) {
        validateRange(from, to);
        return saleRepository.getAll(contactId, type, from, to);
    }

    /** Historial paginado. Tamaño máximo de página: {@value #MAX_PAGE_SIZE}. */
    public PageResult<Sale> findSalesPage(Integer contactId, SaleType type, LocalDate from, LocalDate to,
                                          int page, int size) {
        validateRange(from, to);
        if (page < 0) {
            throw new IllegalArgumentException("El número de página no puede ser negativo");
        }
        if (size < 1) {
            throw new IllegalArgumentException("El tamaño de página debe ser al menos 1");
        }
        if (size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    "El tamaño de página no puede superar " + MAX_PAGE_SIZE);
        }
        return saleRepository.getPage(contactId, type, from, to, page, size);
    }

    public Sale getSaleById(Integer id) {
        return saleRepository.getById(id);
    }

    /**
     * Plántulas disponibles para vender en una siembra: las de la última visita
     * activa menos lo ya vendido. Si no hay visitas, no hay disponibilidad.
     */
    public Integer getAvailability(Integer sowingId) {
        Sowing sowing = requireSowing(sowingId);
        return available(sowing);
    }

    /** Cantidad ya vendida con ventas activas. */
    public Integer getSoldQuantity(Integer sowingId) {
        return saleRepository.getSoldQuantity(sowingId);
    }

    /** Desglose de la disponibilidad: plántulas de la última visita, vendidas y restantes. */
    public Availability getAvailabilityDetail(Integer sowingId) {
        Sowing sowing = requireSowing(sowingId);
        Integer seedlings = visitService.getLatestAvailableSeedlings(sowing.getId());
        int sold = saleRepository.getSoldQuantity(sowing.getId());
        int remaining = seedlings == null ? 0 : Math.max(0, seedlings - sold);
        return new Availability(sowing.getId(), seedlings, sold, remaining);
    }

    /** Resultado del cálculo de disponibilidad de una siembra. */
    public record Availability(Integer sowingId, Integer seedlingsAvailable,
                               Integer soldQuantity, Integer available) {
    }

    @Transactional
    public Sale registerDirectSale(Integer contactId, Integer sowingId, Integer quantity,
                                   BigDecimal unitPrice, LocalDate saleDate, String notes) {
        Sowing sowing = requireSowingForUpdate(sowingId);
        requireClient(contactId);
        requireSellable(sowing);
        validateQuantity(quantity, sowing, 0);
        BigDecimal price = validateUnitPrice(unitPrice);
        LocalDate date = validateSaleDate(saleDate, sowing);

        if (hasLiveReservation(sowingId)) {
            throw new IllegalStateException("La siembra " + sowingId
                    + " tiene una reserva vigente; su producción no se puede vender directo."
                    + " Cancela o entrega la reserva primero.");
        }

        Sale sale = new Sale();
        sale.setContactId(contactId);
        sale.setSowingId(sowingId);
        sale.setReservationId(null);
        sale.setSaleDate(date);
        sale.setQuantity(quantity);
        sale.setUnitPrice(price);
        sale.setTotal(price.multiply(BigDecimal.valueOf(quantity)));
        sale.setSaleType(SaleType.DIRECT);
        sale.setNotes(cleanNotes(notes));
        sale.setActive(true);
        return saleRepository.save(sale);
    }

    @Transactional
    public Sale registerReservationSale(Integer reservationId, Integer quantity,
                                        BigDecimal unitPrice, LocalDate saleDate, String notes) {
        if (reservationId == null) {
            throw new IllegalArgumentException("La reserva es obligatoria para una venta de reserva");
        }
        Reservation reservation = reservationService.getReservationById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reserva no encontrada: " + reservationId);
        }
        if (reservation.getReservationStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("No se puede entregar una reserva cancelada");
        }

        Sowing sowing = requireSowingForUpdate(reservation.getSowingId());
        requireClient(reservation.getClientId());
        requireSellable(sowing);

        Sale sale = saleRepository.getByReservationIdIncludingInactive(reservationId);
        int alreadyCounted = sale != null && !sale.isCancelled() && sale.getQuantity() != null
                ? sale.getQuantity() : 0;
        validateQuantity(quantity, sowing, alreadyCounted);

        BigDecimal price = validateUnitPrice(unitPrice);
        LocalDate date = validateSaleDate(saleDate, sowing);

        if (sale == null) {
            sale = new Sale();
        }
        sale.setReservationId(reservationId);
        sale.setContactId(reservation.getClientId());
        sale.setSowingId(sowing.getId());
        sale.setSaleDate(date);
        sale.setQuantity(quantity);
        sale.setUnitPrice(price);
        sale.setTotal(price.multiply(BigDecimal.valueOf(quantity)));
        sale.setSaleType(SaleType.RESERVATION);
        sale.setNotes(cleanNotes(notes));
        sale.setActive(true);

        Sale saved = saleRepository.save(sale);
        reservationService.markDelivered(reservationId);
        return saved;
    }

    /** Anula una venta (no se borra: el historial debe conservarse). */
    @Transactional
    public Sale cancelSale(Integer id) {
        Sale sale = saleRepository.getById(id);
        if (sale == null) {
            throw new IllegalArgumentException("Venta no encontrada: " + id);
        }
        if (sale.isCancelled()) {
            throw new IllegalStateException("La venta " + id + " ya está anulada");
        }
        sale.setActive(false);
        return saleRepository.save(sale);
    }

    private Integer available(Sowing sowing) {
        return available(sowing, 0);
    }

    private Integer available(Sowing sowing, int alreadyCountedInSold) {
        Integer seedlings = visitService.getLatestAvailableSeedlings(sowing.getId());
        if (seedlings == null) {
            return 0;
        }
        int sold = saleRepository.getSoldQuantity(sowing.getId());
        int netSold = Math.max(0, sold - Math.max(0, alreadyCountedInSold));
        return Math.max(0, seedlings - netSold);
    }

    private boolean hasLiveReservation(Integer sowingId) {
        return reservationService.findReservations(sowingId, null, null).stream()
                .anyMatch(Reservation::isLive);
    }

    private void validateQuantity(Integer quantity, Sowing sowing, int alreadyCountedInSold) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("La cantidad vendida debe ser mayor que cero");
        }
        if (sowing.getQuantitySown() != null && quantity > sowing.getQuantitySown()) {
            throw new IllegalArgumentException("La cantidad vendida (" + quantity
                    + ") no puede superar la cantidad sembrada (" + sowing.getQuantitySown() + ")");
        }
        int available = available(sowing, alreadyCountedInSold);
        if (quantity > available) {
            throw new IllegalStateException("No hay plántulas suficientes en la siembra "
                    + sowing.getId() + ": disponible " + available + ", solicitado " + quantity
                    + ". Registra una visita o reduce la cantidad.");
        }
    }

    private BigDecimal validateUnitPrice(BigDecimal unitPrice) {
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio unitario es obligatorio y no puede ser negativo");
        }
        if (unitPrice.scale() > 2) {
            throw new IllegalArgumentException("El precio unitario admite como máximo dos decimales");
        }
        return unitPrice;
    }

    private LocalDate validateSaleDate(LocalDate saleDate, Sowing sowing) {
        LocalDate date = saleDate != null ? saleDate : LocalDate.now();
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de la venta no puede ser futura");
        }
        if (sowing.getSowingDate() != null && date.isBefore(sowing.getSowingDate())) {
            throw new IllegalArgumentException("La fecha de la venta no puede ser anterior a la fecha de siembra");
        }
        return date;
    }

    private void validateRange(LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("La fecha inicial del filtro no puede ser posterior a la final");
        }
    }

    private void requireClient(Integer contactId) {
        try {
            contactService.assertHasRole(contactId, ContactRole.CLIENT);
        } catch (ContactNotFoundException e) {
            throw new IllegalArgumentException("Cliente no encontrado: " + contactId);
        }
    }

    private void requireSellable(Sowing sowing) {
        if (Boolean.FALSE.equals(sowing.getActive())) {
            throw new IllegalStateException("La siembra " + sowing.getId() + " está inactiva");
        }
        if (!SELLABLE.contains(sowing.getStatus())) {
            throw new IllegalStateException("No se puede vender la siembra " + sowing.getId()
                    + " porque está en estado " + sowing.getStatus()
                    + ". Solo se permite vender siembras PLANNED, IN_PROGRESS o COMPLETED");
        }
    }

    private Sowing requireSowing(Integer sowingId) {
        Sowing sowing = sowingRepository.getById(sowingId);
        if (sowing == null) {
            throw new IllegalArgumentException("Siembra no encontrada: " + sowingId);
        }
        return sowing;
    }

    private Sowing requireSowingForUpdate(Integer sowingId) {
        Sowing sowing = sowingRepository.getByIdForUpdate(sowingId);
        if (sowing == null) {
            throw new IllegalArgumentException("Siembra no encontrada: " + sowingId);
        }
        return sowing;
    }

    private String cleanNotes(String notes) {
        if (notes == null || notes.isBlank()) {
            return null;
        }
        String clean = notes.trim();
        if (clean.length() > MAX_NOTES_LENGTH) {
            throw new IllegalArgumentException(
                    "Las observaciones no pueden superar los " + MAX_NOTES_LENGTH + " caracteres");
        }
        return clean;
    }
}
