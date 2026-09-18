package co.improsvita.web.dto;

import java.time.LocalDateTime;

public class SeedRequest {

    private String name;
    private Integer supplierId;
    private Integer quantity;
    private String type;
    private LocalDateTime acquisitionDate;
    private LocalDateTime expirationDate;

    public SeedRequest() {
    }

    public SeedRequest(String name, Integer supplierId, Integer quantity, String type, LocalDateTime acquisitionDate, LocalDateTime expirationDate) {
        this.name = name;
        this.supplierId = supplierId;
        this.quantity = quantity;
        this.type = type;
        this.acquisitionDate = acquisitionDate;
        this.expirationDate = expirationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(LocalDateTime acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }
}
