package co.improsvita.domain.model;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

public class Contact {

    private Integer id;
    private String name;
    private String phone;
    private String email;
    private Boolean active;
    private Set<ContactRole> roles = EnumSet.noneOf(ContactRole.class);
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdated;

    public Contact() {
    }

    public Contact(Integer id, String name, String phone, String email, Boolean active,
                   Set<ContactRole> roles, LocalDateTime createdDate, LocalDateTime lastUpdated) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.active = active;
        this.roles = roles != null ? EnumSet.copyOf(roles) : EnumSet.noneOf(ContactRole.class);
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<ContactRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<ContactRole> roles) {
        this.roles = roles != null && !roles.isEmpty()
                ? EnumSet.copyOf(roles)
                : EnumSet.noneOf(ContactRole.class);
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
