package co.improsvita.web.dto;

import java.math.BigDecimal;

public class BedResponse {

    private Integer id;
    private String code;
    private BigDecimal maxCapacity;
    private Boolean active;

    public BedResponse() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(BigDecimal maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}