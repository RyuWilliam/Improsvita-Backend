package co.improsvita.domain.model;

public class Seed {

    private Integer id;
    private String name;
    private SeedTypeCatalog seedType;
    private String description;
    private Boolean active;

    private Long totalAvailable;

    public Seed() {
    }

    public Seed(Integer id, String name, SeedTypeCatalog seedType, String description, Boolean active) {
        this.id = id;
        this.name = name;
        this.seedType = seedType;
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

    public SeedTypeCatalog getSeedType() {
        return seedType;
    }

    public void setSeedType(SeedTypeCatalog seedType) {
        this.seedType = seedType;
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

    public Long getTotalAvailable() {
        return totalAvailable;
    }

    public void setTotalAvailable(Long totalAvailable) {
        this.totalAvailable = totalAvailable;
    }
}