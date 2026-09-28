package co.improsvita.domain.model;

import java.math.BigDecimal;

public class Seed {

    private Integer id;
    private String name;
    private SeedType type;
    private String description;
    private Boolean active;

    private BigDecimal totalAvailable;

    public Seed() {
    }

    public Seed(Integer id, String name, SeedType type, String description, Boolean active) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
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

    public SeedType getType() {
        return type;
    }

    public void setType(SeedType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public BigDecimal getTotalAvailable() {
        return totalAvailable;
    }

    public void setTotalAvailable(BigDecimal totalAvailable) {
        this.totalAvailable = totalAvailable;
    }
}