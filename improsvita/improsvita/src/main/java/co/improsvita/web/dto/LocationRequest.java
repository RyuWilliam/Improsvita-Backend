package co.improsvita.web.dto;

import jakarta.validation.constraints.NotBlank;

public class LocationRequest {

    @NotBlank(message = "El nombre de la ubicación es obligatorio")
    private String locationName;

    private Boolean active;

    public LocationRequest() {
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}