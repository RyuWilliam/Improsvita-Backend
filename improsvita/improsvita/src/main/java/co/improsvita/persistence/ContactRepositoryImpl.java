package co.improsvita.persistence;

import co.improsvita.domain.model.Contact;
import co.improsvita.domain.model.ContactRole;
import co.improsvita.domain.repository.ContactRepository;
import co.improsvita.persistence.crud.ContactJpaRepository;
import co.improsvita.persistence.entities.ContactEntity;
import co.improsvita.persistence.mapper.ContactMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class ContactRepositoryImpl implements ContactRepository {

    private final ContactJpaRepository contactJpaRepository;

    public ContactRepositoryImpl(ContactJpaRepository contactJpaRepository) {
        this.contactJpaRepository = contactJpaRepository;
    }

    @Override
    public List<Contact> getAll(ContactRole role, boolean includeInactive) {
        List<ContactEntity> entities;
        if (role == null) {
            entities = includeInactive
                    ? contactJpaRepository.findAll()
                    : contactJpaRepository.findByActiveTrue();
        } else {
            entities = includeInactive
                    ? contactJpaRepository.findByRole(role)
                    : contactJpaRepository.findActiveByRole(role);
        }
        return entities.stream()
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
    public Contact getByEmail(String email) {
        return contactJpaRepository.findByEmail(email)
                .map(ContactMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Contact getByName(String name) {
        return contactJpaRepository.findFirstByName(name)
                .map(ContactMapper::toDomain)
                .orElse(null);
    }

    @Override
    @Transactional
    public Contact save(Contact contact) {
        ContactEntity entity;
        if (contact.getId() == null) {
            entity = new ContactEntity();
        } else {
            entity = contactJpaRepository.findById(contact.getId()).orElse(null);
            if (entity == null) {
                return null;
            }
        }
        ContactMapper.applyToEntity(entity, contact);
        ContactEntity saved = contactJpaRepository.save(entity);
        return ContactMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        contactJpaRepository.deleteById(id);
    }
}
