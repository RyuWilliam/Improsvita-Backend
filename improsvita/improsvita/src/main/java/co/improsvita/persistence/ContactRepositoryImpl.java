package co.improsvita.persistence;

import co.improsvita.domain.model.Contact;
import co.improsvita.domain.repository.ContactRepository;
import co.improsvita.persistence.crud.ContactJpaRepository;
import co.improsvita.persistence.mapper.ContactMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ContactRepositoryImpl implements ContactRepository {

    private final ContactJpaRepository contactJpaRepository;

    public ContactRepositoryImpl(ContactJpaRepository contactJpaRepository) {
        this.contactJpaRepository = contactJpaRepository;
    }

    @Override
    public List<Contact> getAll() {
        return contactJpaRepository.findAll()
                .stream()
                .map(ContactMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Contact getById(Integer id) {
        return contactJpaRepository.findById(id)
                .map(ContactMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Contact getByDocumentNumber(String documentNumber) {
        return contactJpaRepository.findByDocumentNumberIgnoreCase(documentNumber)
                .map(ContactMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Contact getByEmail(String email) {
        return contactJpaRepository.findByEmailIgnoreCase(email)
                .map(ContactMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Contact save(Contact contact) {
        var saved = contactJpaRepository.save(ContactMapper.toEntity(contact));
        return ContactMapper.toDomain(saved);
    }

    @Override
    public void deleteById(Integer id) {
        contactJpaRepository.deleteById(id);
    }
}
