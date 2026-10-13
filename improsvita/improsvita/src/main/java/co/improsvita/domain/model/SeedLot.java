package co.improsvita.domain.model;

import java.time.LocalDate;

public class SeedLot {

    private Integer id;
    private Integer lotNumber;
    private Integer seedId;
    private Integer locationId;
    private Integer germinationPercentage;
    private LocalDate entryDate;
    private LocalDate dueDate;
    private Integer initialQuantity;
    private Integer availableQuantity;
    private SeedLotStatus status;

    public SeedLot() {
    }

    public SeedLot(Integer id, Integer lotNumber, Integer seedId, Integer locationId,
                   Integer germinationPercentage,
                   LocalDate entryDate, LocalDate dueDate,
                   Integer initialQuantity, Integer availableQuantity,
                   SeedLotStatus status) {
        this.id = id;
        this.lotNumber = lotNumber;
        this.seedId = seedId;
        this.locationId = locationId;
        this.germinationPercentage = germinationPercentage;
        this.entryDate = entryDate;
        this.dueDate = dueDate;
        this.initialQuantity = initialQuantity;
        this.availableQuantity = availableQuantity;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(Integer lotNumber) {
        this.lotNumber = lotNumber;
    }

    public Integer getSeedId() {
        return seedId;
    }

    public void setSeedId(Integer seedId) {
        this.seedId = seedId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
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

    public Integer getGerminationPercentage() {
        return germinationPercentage;
    }

    public void setGerminationPercentage(Integer germinationPercentage) {
        this.germinationPercentage = germinationPercentage;
    }

    public Integer getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(Integer initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public SeedLotStatus getStatus() {
        return status;
    }

    public void setStatus(SeedLotStatus status) {
        this.status = status;
    }
}