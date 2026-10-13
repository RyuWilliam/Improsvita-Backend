package co.improsvita.web.dto;

import java.time.LocalDate;

public class SowingResponse {

    private Integer id;
    private Integer lotId;
    private Integer bedId;
    private Integer quantitySown;
    private LocalDate sowingDate;
    private LocalDate expectedGerminationDate;
    private String status;
    private String notes;
    private Boolean active;

    public SowingResponse() {
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

    public Integer getQuantitySown() {
        return quantitySown;
    }

    public void setQuantitySown(Integer quantitySown) {
        this.quantitySown = quantitySown;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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