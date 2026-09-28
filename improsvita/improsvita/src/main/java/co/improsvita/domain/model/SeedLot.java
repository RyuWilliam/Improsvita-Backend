package co.improsvita.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SeedLot {

    private Integer id;
    private Integer lotNumber;
    private Integer seedId;
    private Integer locationId;
    private LocalDate entryDate;
    private LocalDate dueDate;
    private BigDecimal initialQuantity;
    private BigDecimal availableQuantity;
    private SeedLotStatus status;

    public SeedLot() {
    }

    public SeedLot(Integer id, Integer lotNumber, Integer seedId, Integer locationId,
                   LocalDate entryDate, LocalDate dueDate,
                   BigDecimal initialQuantity, BigDecimal availableQuantity,
                   SeedLotStatus status) {
        this.id = id;
        this.lotNumber = lotNumber;
        this.seedId = seedId;
        this.locationId = locationId;
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

    public BigDecimal getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(BigDecimal initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(BigDecimal availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public SeedLotStatus getStatus() {
        return status;
    }

    public void setStatus(SeedLotStatus status) {
        this.status = status;
    }
}