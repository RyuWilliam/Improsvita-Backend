package co.improsvita.domain.service;

import co.improsvita.domain.model.Contact;
import co.improsvita.domain.repository.ContactRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class ContactService {

    private static final int MAX_NAME_LENGTH = 150;
    private static final int MAX_DOCUMENT_TYPE_LENGTH = 20;
    private static final int MAX_DOCUMENT_NUMBER_LENGTH = 30;
    private static final int MAX_PHONE_LENGTH = 20;
    private static final int MAX_EMAIL_LENGTH = 150;
    private static final int MAX_ADDRESS_LENGTH = 255;

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public List<Contact> getAllContacts() {
        return contactRepository.getAll();
    }

    public Contact getContactById(Integer id) {
        return contactRepository.getById(id);
    }

    public Contact getContactByDocumentNumber(String documentNumber) {
        if (documentNumber == null || documentNumber.isBlank()) {
            throw new IllegalArgumentException("El número de documento es obligatorio");
        }
        return contactRepository.getByDocumentNumber(documentNumber.trim());
    }

    public Contact createContact(Contact contact) {
        normalize(contact);
        validate(contact);
        ensureUnique(contact, null);
        if (contact.getActive() == null) {
            contact.setActive(true);
        }
        return contactRepository.save(contact);
    }

    public Contact updateContact(Integer id, Contact contact) {
        Contact existing = requireContact(id);
        normalize(contact);
        validate(contact);
        ensureUnique(contact, id);

        existing.setName(contact.getName());
        existing.setDocumentType(contact.getDocumentType());
        existing.setDocumentNumber(contact.getDocumentNumber());
        existing.setPhone(contact.getPhone());
        existing.setEmail(contact.getEmail());
        existing.setAddress(contact.getAddress());
        if (contact.getActive() != null) {
            existing.setActive(contact.getActive());
        }
        return contactRepository.save(existing);
    }

    public void deleteContact(Integer id) {
        requireContact(id);
        contactRepository.deleteById(id);
    }

    private Contact requireContact(Integer id) {
        Contact contact = contactRepository.getById(id);
        if (contact == null) {
            throw new IllegalArgumentException("Contacto no encontrado: " + id);
        }
        return contact;
    }

    private void ensureUnique(Contact contact, Integer currentId) {
        Contact sameDocument = contactRepository.getByDocumentNumber(contact.getDocumentNumber());
        if (sameDocument != null && !Objects.equals(sameDocument.getId(), currentId)) {
            throw new IllegalStateException(
                    "Ya existe un contacto con el documento " + contact.getDocumentNumber());
        }
        if (contact.getEmail() != null) {
            Contact sameEmail = contactRepository.getByEmail(contact.getEmail());
            if (sameEmail != null && !Objects.equals(sameEmail.getId(), currentId)) {
                throw new IllegalStateException(
                        "Ya existe un contacto con el correo " + contact.getEmail());
            }
        }
    }

    private void normalize(Contact contact) {
        contact.setName(trimToNull(contact.getName()));
        contact.setDocumentType(trimToNull(contact.getDocumentType()));
        contact.setDocumentNumber(trimToNull(contact.getDocumentNumber()));
        contact.setPhone(trimToNull(contact.getPhone()));
        contact.setEmail(trimToNull(contact.getEmail()));
        contact.setAddress(trimToNull(contact.getAddress()));

        if (contact.getDocumentType() != null) {
            contact.setDocumentType(contact.getDocumentType().toUpperCase(Locale.ROOT));
        }
        if (contact.getEmail() != null) {
            contact.setEmail(contact.getEmail().toLowerCase(Locale.ROOT));
        }
    }

    private void validate(Contact contact) {
        validateRequired(contact.getName(), "El nombre del contacto", MAX_NAME_LENGTH);
        validateRequired(contact.getDocumentType(), "El tipo de documento", MAX_DOCUMENT_TYPE_LENGTH);
        validateRequired(contact.getDocumentNumber(), "El número de documento", MAX_DOCUMENT_NUMBER_LENGTH);
        validateLength(contact.getPhone(), "El teléfono", MAX_PHONE_LENGTH);
        validateLength(contact.getEmail(), "El correo", MAX_EMAIL_LENGTH);
        validateLength(contact.getAddress(), "La dirección", MAX_ADDRESS_LENGTH);
    }

    private void validateRequired(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " es obligatorio");
        }
        validateLength(value, field, maxLength);
    }

    private void validateLength(String value, String field, int maxLength) {
        if (value != null && value.length() > maxLength) {
            throw new IllegalArgumentException(
                    field + " no puede superar los " + maxLength + " caracteres");
        }
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
