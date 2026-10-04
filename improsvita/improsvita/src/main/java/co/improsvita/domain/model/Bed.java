package co.improsvita.domain.model;

import java.math.BigDecimal;

public class Bed {

    private Integer id;
    private String code;
    private BigDecimal maxCapacity;
    private Boolean active;

    public Bed() {
    }

    public Bed(Integer id, String code, BigDecimal maxCapacity, Boolean active) {
        this.id = id;
        this.code = code;
        this.maxCapacity = maxCapacity;
        this.active = active;
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