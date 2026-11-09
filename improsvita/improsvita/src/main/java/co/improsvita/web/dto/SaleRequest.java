package co.improsvita.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Venta directa: se indica la siembra de la que sale el stock.
 * Para la entrega de una reserva se usa {@link ReservationSaleRequest}.
 */
public class SaleRequest {

    @NotNull(message = "El cliente es obligatorio")
    private Integer contactId;

    @NotNull(message = "La siembra es obligatoria")
    private Integer sowingId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor que cero")
    private Integer quantity;

    @NotNull(message = "El precio unitario es obligatorio")
    private BigDecimal unitPrice;

    private LocalDate saleDate;

    private String notes;

    public SaleRequest() {
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

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
