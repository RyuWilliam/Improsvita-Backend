package co.improsvita.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Sowing {

    private Integer id;
    private Integer lotId;
    private Integer bedId;
    private BigDecimal quantitySown;
    private BigDecimal germinatedQuantity;
    private LocalDate sowingDate;
    private LocalDate expectedGerminationDate;
    private SowingStatus status;
    private String notes;
    private Boolean active;

    public Sowing() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public BigDecimal getQuantitySown() {
        return quantitySown;
    }

    public void setQuantitySown(BigDecimal quantitySown) {
        this.quantitySown = quantitySown;
    }

    public BigDecimal getGerminatedQuantity() {
        return germinatedQuantity;
    }

    public void setGerminatedQuantity(BigDecimal germinatedQuantity) {
        this.germinatedQuantity = germinatedQuantity;
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

    public SowingStatus getStatus() {
        return status;
    }

    public void setStatus(SowingStatus status) {
        this.status = status;
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
}