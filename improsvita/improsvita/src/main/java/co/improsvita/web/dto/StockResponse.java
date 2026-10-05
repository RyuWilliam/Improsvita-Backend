package co.improsvita.web.dto;

import java.math.BigDecimal;

public class StockResponse {

    private Integer seedId;
    private BigDecimal totalAvailable;

    public StockResponse() {
    }

    public StockResponse(Integer seedId, BigDecimal totalAvailable) {
        this.seedId = seedId;
        this.totalAvailable = totalAvailable;
    }

    public Integer getSeedId() {
        return seedId;
    }

    public void setSeedId(Integer seedId) {
        this.seedId = seedId;
    }

    public BigDecimal getTotalAvailable() {
        return totalAvailable;
    }

    public void setTotalAvailable(BigDecimal totalAvailable) {
        this.totalAvailable = totalAvailable;
    }
}