package co.improsvita.web.dto;

public class StockResponse {

    private Integer seedId;
    private Long totalAvailable;

    public StockResponse() {
    }

    public StockResponse(Integer seedId, Long totalAvailable) {
        this.seedId = seedId;
        this.totalAvailable = totalAvailable;
    }

    public Integer getSeedId() {
        return seedId;
    }

    public void setSeedId(Integer seedId) {
        this.seedId = seedId;
    }

    public Long getTotalAvailable() {
        return totalAvailable;
    }

    public void setTotalAvailable(Long totalAvailable) {
        this.totalAvailable = totalAvailable;
    }
}