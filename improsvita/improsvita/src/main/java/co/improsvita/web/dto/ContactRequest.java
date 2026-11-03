package co.improsvita.web.dto;

import co.improsvita.domain.model.ContactRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public class ContactRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String phone;

    @NotBlank
    @Email
    private String email;

    @NotEmpty
    private Set<ContactRole> roles;

    public ContactRequest() {
    }

    public ContactRequest(String name, String phone, String email, Set<ContactRole> roles) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.roles = roles;
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

    public Set<ContactRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<ContactRole> roles) {
        this.roles = roles;
    }
}
