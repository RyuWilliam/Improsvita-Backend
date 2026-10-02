package co.improsvita.domain.model;

public class Location {

    private Integer id;
    private String locationName;
    private Boolean active;

    public Location() {
    }

    public Location(Integer id, String locationName, Boolean active) {
        this.id = id;
        this.locationName = locationName;
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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