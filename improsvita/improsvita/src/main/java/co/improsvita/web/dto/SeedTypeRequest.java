package co.improsvita.web.dto;

import jakarta.validation.constraints.NotBlank;

public class SeedTypeRequest {

    @NotBlank(message = "El nombre del tipo de semilla es obligatorio")
    private String name;

    private String description;

    public SeedTypeRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}