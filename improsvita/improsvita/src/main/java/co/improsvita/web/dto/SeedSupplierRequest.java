package co.improsvita.web.dto;

public class SeedSupplierRequest {

    private Integer seedId;
    private Integer supplierId;

    public SeedSupplierRequest() {
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
}