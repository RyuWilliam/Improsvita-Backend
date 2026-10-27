package co.improsvita.persistence.mapper;

import co.improsvita.domain.model.Contact;
import co.improsvita.persistence.entities.ContactEntity;

public class ContactMapper {

    private ContactMapper() {
    }

    public static Contact toDomain(ContactEntity entity) {
        if (entity == null) return null;
        Contact contact = new Contact();
        contact.setId(entity.getContactId());
        contact.setName(entity.getName());
        contact.setDocumentType(entity.getDocumentType());
        contact.setDocumentNumber(entity.getDocumentNumber());
        contact.setPhone(entity.getPhone());
        contact.setEmail(entity.getEmail());
        contact.setAddress(entity.getAddress());
        contact.setActive(entity.getActive());
        return contact;
    }

    public static ContactEntity toEntity(Contact domain) {
        if (domain == null) return null;
        ContactEntity entity = new ContactEntity();
        entity.setContactId(domain.getId());
        entity.setName(domain.getName());
        entity.setDocumentType(domain.getDocumentType());
        entity.setDocumentNumber(domain.getDocumentNumber());
        entity.setPhone(domain.getPhone());
        entity.setEmail(domain.getEmail());
        entity.setAddress(domain.getAddress());
        entity.setActive(domain.getActive());
        return entity;
    }
}
