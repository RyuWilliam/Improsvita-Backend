package co.improsvita.web.dto;

import java.math.BigDecimal;

public class SeedResponse {

    private Integer id;
    private String name;
    private String type;
    private String description;
    private BigDecimal totalAvailable;
    private Boolean active;

    public SeedResponse() {
    }

    public SeedResponse(Integer id, String name, String type, String description, BigDecimal totalAvailable, Boolean active) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.totalAvailable = totalAvailable;
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTotalAvailable() {
        return totalAvailable;
    }

    public void setTotalAvailable(BigDecimal totalAvailable) {
        this.totalAvailable = totalAvailable;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}