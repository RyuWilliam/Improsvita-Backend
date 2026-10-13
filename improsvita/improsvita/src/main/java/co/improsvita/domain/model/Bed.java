package co.improsvita.domain.model;

public class Bed {

    private Integer id;
    private String code;
    private Integer maxCapacity;
    private Boolean active;

    public Bed() {
    }

    public Bed(Integer id, String code, Integer maxCapacity, Boolean active) {
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