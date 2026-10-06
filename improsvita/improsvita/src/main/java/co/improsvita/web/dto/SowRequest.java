package co.improsvita.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SowRequest {

    private Integer lotId;
    private Integer bedId;
    private BigDecimal quantity;
    private LocalDate sowingDate;
    private LocalDate expectedGerminationDate;
    private String notes;

    public SowRequest() {
    }

    public Integer getLotId() {
        return lotId;
    }

    public void setLotId(Integer lotId) {
        this.lotId = lotId;
    }

    public Integer getBedId() {
        return bedId;
    }

    public void setBedId(Integer bedId) {
        this.bedId = bedId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public LocalDate getSowingDate() {
        return sowingDate;
    }

    public void setSowingDate(LocalDate sowingDate) {
        this.sowingDate = sowingDate;
    }

    public LocalDate getExpectedGerminationDate() {
        return expectedGerminationDate;
    }

    public void setExpectedGerminationDate(LocalDate expectedGerminationDate) {
        this.expectedGerminationDate = expectedGerminationDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}