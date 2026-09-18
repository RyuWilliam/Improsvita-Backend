package co.improsvita.web.dto;

import java.time.LocalDateTime;

public class SeedResponse {

    private Integer id;
    private String name;
    private SupplierResponse supplier;
    private Integer quantity;
    private String type;
    private LocalDateTime acquisitionDate;
    private LocalDateTime expirationDate;
    private Boolean active;

    public SeedResponse() {
    }

    public SeedResponse(Integer id, String name, SupplierResponse supplier, Integer quantity, String type, LocalDateTime acquisitionDate, LocalDateTime expirationDate, Boolean active) {
        this.id = id;
        this.name = name;
        this.supplier = supplier;
        this.quantity = quantity;
        this.type = type;
        this.acquisitionDate = acquisitionDate;
        this.expirationDate = expirationDate;
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SupplierResponse getSupplier() {
        return supplier;
    }

    public void setSupplier(SupplierResponse supplier) {
        this.supplier = supplier;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
