package co.improsvita.persistence.mapper;

import co.improsvita.domain.model.Contact;
import co.improsvita.domain.model.ContactRole;
import co.improsvita.persistence.entities.ContactEntity;

import java.util.HashSet;
import java.util.Set;

public class ContactMapper {

    private ContactMapper() {
    }

    public static Contact toDomain(ContactEntity entity) {
        if (entity == null) return null;
        Contact contact = new Contact();
        contact.setId(entity.getContactId());
        contact.setName(entity.getName());
        contact.setPhone(entity.getPhone());
        contact.setEmail(entity.getEmail());
        contact.setActive(entity.getActive());
        contact.setRoles(entity.getRoles());
        contact.setCreatedDate(entity.getCreatedDate());
        contact.setLastUpdated(entity.getLastUpdated());
        return contact;
    }

    public static void applyToEntity(ContactEntity entity, Contact contact) {
        entity.setName(contact.getName());
        entity.setPhone(contact.getPhone());
        entity.setEmail(contact.getEmail());
        if (contact.getActive() != null) {
            entity.setActive(contact.getActive());
        }
        applyRoles(entity, contact.getRoles());
    }

    private static void applyRoles(ContactEntity entity, Set<ContactRole> roles) {
        Set<ContactRole> desired = roles != null ? new HashSet<>(roles) : new HashSet<>();
        entity.getRoles().clear();
        entity.getRoles().addAll(desired);
    }
}
