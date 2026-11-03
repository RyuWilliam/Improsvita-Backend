package co.improsvita.domain.repository;

import co.improsvita.domain.model.Contact;
import co.improsvita.domain.model.ContactRole;

import java.util.List;

public interface ContactRepository {
    List<Contact> getAll(ContactRole role, boolean includeInactive);
    Contact getById(Integer id);
    Contact getByEmail(String email);
    Contact getByName(String name);
    Contact save(Contact contact);
    void deleteById(Integer id);
}
