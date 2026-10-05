package co.improsvita.domain.model;

public class SeedSupplier {

    private Integer id;
    private Integer seedId;
    private Integer supplierId;
    private Boolean active;

    public SeedSupplier() {
    }

    public SeedSupplier(Integer id, Integer seedId, Integer supplierId, Boolean active) {
        this.id = id;
        this.seedId = seedId;
        this.supplierId = supplierId;
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}