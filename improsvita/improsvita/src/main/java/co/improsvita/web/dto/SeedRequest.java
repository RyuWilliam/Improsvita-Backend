package co.improsvita.web.dto;

import jakarta.validation.constraints.NotBlank;

public class SeedRequest {

    @NotBlank(message = "El nombre de la semilla es obligatorio")
    private String name;

    private String type;
    private Integer typeId;
    private String description;

    public SeedRequest() {
    }

    public SeedRequest(String name, String type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
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
    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
}