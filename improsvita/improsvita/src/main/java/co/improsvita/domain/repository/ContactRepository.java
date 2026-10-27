package co.improsvita.domain.repository;

import co.improsvita.domain.model.Contact;

import java.util.List;

public interface ContactRepository {
    List<Contact> getAll();
    Contact getById(Integer id);
    Contact getByDocumentNumber(String documentNumber);
    Contact getByEmail(String email);
    Contact save(Contact contact);
    void deleteById(Integer id);
}
