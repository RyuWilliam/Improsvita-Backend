package co.improsvita.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Venta de plántulas.
 * <p>
 * Siempre se origina en una siembra ({@code sowingId}), que es donde está el
 * stock. {@code reservationId} es nulo en una venta directa y apunta a la
 * reserva cuando la venta es su entrega. {@code total} se calcula en el
 * servidor a partir de {@code quantity * unitPrice}.
 */
public class Sale {

    private Integer id;
    private Integer contactId;
    private Integer sowingId;
    private Integer reservationId;
    private LocalDate saleDate;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal total;
    private SaleType saleType;
    private String notes;
    private Boolean active;

    public Sale() {
    }

    /** Una venta es una transacción cerrada: solo se anula (active = false). */
    public boolean isCancelled() {
        return Boolean.FALSE.equals(active);
    }

    /** Una reserva se factura como máximo una vez mientras la venta siga activa. */
    public boolean billsReservation() {
        return reservationId != null && !isCancelled();
    }

    /** Total esperado para la cantidad y el precio unitario actuales. */
    public BigDecimal expectedTotal() {
        if (quantity == null || unitPrice == null) {
            return null;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getContactId() {
        return contactId;
    }

    public void setContactId(Integer contactId) {
        this.contactId = contactId;
    }

    public Integer getSowingId() {
        return sowingId;
    }

    public void setSowingId(Integer sowingId) {
        this.sowingId = sowingId;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public SaleType getSaleType() {
        return saleType;
    }

    public void setSaleType(SaleType saleType) {
        this.saleType = saleType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sale other)) return false;
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
