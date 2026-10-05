package co.improsvita.web.dto;

import java.math.BigDecimal;

public class GerminationRequest {

    private BigDecimal germinatedQuantity;

    public GerminationRequest() {
    }

    public BigDecimal getGerminatedQuantity() {
        return germinatedQuantity;
    }

    public void setGerminatedQuantity(BigDecimal germinatedQuantity) {
        this.germinatedQuantity = germinatedQuantity;
    }
}