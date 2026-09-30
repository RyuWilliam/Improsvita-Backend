package co.improsvita.web.dto;

public class LocationRequest {

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