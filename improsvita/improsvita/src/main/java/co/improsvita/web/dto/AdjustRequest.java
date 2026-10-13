package co.improsvita.web.dto;

public class AdjustRequest {

    private Integer quantity;
    private String reason;

    public AdjustRequest() {
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
}