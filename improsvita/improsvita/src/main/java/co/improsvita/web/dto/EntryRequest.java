package co.improsvita.web.dto;

import java.time.LocalDate;

public class EntryRequest {

    private Integer seedId;
    private Integer supplierId;
    private Integer locationId;
    private Integer lotNumber;
    private Integer quantity;
    private Integer germinationPercentage;
    private LocalDate entryDate;
    private LocalDate dueDate;

    public EntryRequest() {
    }

    public Integer getSeedId() {
        return seedId;
    }

    public void setSeedId(Integer seedId) {
        this.seedId = seedId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(Integer lotNumber) {
        this.lotNumber = lotNumber;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getGerminationPercentage() {
        return germinationPercentage;
    }

    public void setGerminationPercentage(Integer germinationPercentage) {
        this.germinationPercentage = germinationPercentage;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}