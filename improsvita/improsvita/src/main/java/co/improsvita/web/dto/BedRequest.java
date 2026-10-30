package co.improsvita.web.dto;

import jakarta.validation.constraints.NotBlank;

public class BedRequest {

    @NotBlank(message = "El código de la cama es obligatorio")
    private String code;

    private Integer maxCapacity;
    private Boolean active;

    public BedRequest() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}