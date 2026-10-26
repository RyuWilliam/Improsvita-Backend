package co.improsvita.web.dto;

public class LotAdjustRequest {

    private Integer quantity;
    private String reason;
    private Integer germinationPercentage;

    public LotAdjustRequest() {
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getGerminationPercentage() {
        return germinationPercentage;
    }

    public void setGerminationPercentage(Integer germinationPercentage) {
        this.germinationPercentage = germinationPercentage;
    }
}
