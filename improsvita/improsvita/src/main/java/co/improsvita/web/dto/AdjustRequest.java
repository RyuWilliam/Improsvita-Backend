package co.improsvita.web.dto;

import java.math.BigDecimal;

public class AdjustRequest {

    private BigDecimal quantity;
    private String reason;

    public AdjustRequest() {
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}