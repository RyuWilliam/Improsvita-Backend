package co.improsvita.domain.service;

import co.improsvita.domain.model.Contact;
import co.improsvita.domain.model.ContactRole;
import co.improsvita.domain.repository.ContactRepository;
import co.improsvita.domain.repository.ReservationRepository;
import co.improsvita.domain.repository.SeedMovementRepository;
import co.improsvita.domain.repository.SeedSupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final SeedSupplierRepository seedSupplierRepository;
    private final SeedMovementRepository seedMovementRepository;
    private final ReservationRepository reservationRepository;

    public ContactService(ContactRepository contactRepository,
                          SeedSupplierRepository seedSupplierRepository,
                          SeedMovementRepository seedMovementRepository,
                          ReservationRepository reservationRepository) {
        this.contactRepository = contactRepository;
        this.seedSupplierRepository = seedSupplierRepository;
        this.seedMovementRepository = seedMovementRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<Contact> getAllContacts(ContactRole role, boolean includeInactive) {
        return contactRepository.getAll(role, includeInactive);
    }

    public Contact getContactById(Integer id) {
        Contact contact = contactRepository.getById(id);
        if (contact == null) {
            throw new ContactNotFoundException(id);
        }
        return contact;
    }

    public Contact getContactByName(String name) {
        return contactRepository.getByName(name);
    }

    public Contact assertHasRole(Integer contactId, ContactRole role) {
        Contact contact = contactRepository.getById(contactId);
        if (contact == null) {
            throw new ContactNotFoundException(contactId);
        }
        if (contact.getRoles() == null || !contact.getRoles().contains(role)) {
            throw new IllegalArgumentException(
                    "El contacto " + contactId + " no tiene el rol " + role);
        }
        return contact;
    }

    public Contact createContact(Contact contact) {
        requireRoles(contact);
        if (contactRepository.getByEmail(contact.getEmail()) != null) {
            throw new DuplicateEmailException(contact.getEmail());
        }
        if (contact.getActive() == null) {
            contact.setActive(true);
        }
        return contactRepository.save(contact);
    }

    public Contact updateContact(Integer id, Contact contact) {
        requireRoles(contact);
        Contact existing = getContactById(id);

        Contact sameEmail = contactRepository.getByEmail(contact.getEmail());
        if (sameEmail != null && !Objects.equals(sameEmail.getId(), id)) {
            throw new DuplicateEmailException(contact.getEmail());
        }

        if (existing.getRoles().contains(ContactRole.SUPPLIER)
                && !contact.getRoles().contains(ContactRole.SUPPLIER)
                && hasSupplierReferences(id)) {
            throw new ContactRoleInUseException(
                    "No se puede quitar el rol SUPPLIER al contacto " + id
                            + " porque tiene registros en seeds_suppliers o seed_movement");
        }

        if (existing.getRoles().contains(ContactRole.CLIENT)
                && !contact.getRoles().contains(ContactRole.CLIENT)
                && !reservationRepository.getByClientId(id).isEmpty()) {
            throw new ContactRoleInUseException(
                    "No se puede quitar el rol CLIENT al contacto " + id
                            + " porque tiene reservas registradas");
        }

        existing.setName(contact.getName());
        existing.setPhone(contact.getPhone());
        existing.setEmail(contact.getEmail());
        existing.setRoles(contact.getRoles());
        return contactRepository.save(existing);
    }

    public void deleteContact(Integer id) {
        Contact existing = getContactById(id);
        existing.setActive(false);
        contactRepository.save(existing);
    }

    private void requireRoles(Contact contact) {
        if (contact.getRoles() == null || contact.getRoles().isEmpty()) {
            throw new IllegalArgumentException("El contacto debe tener al menos un rol");
        }
    }

    private boolean hasSupplierReferences(Integer contactId) {
        return seedSupplierRepository.existsBySupplierId(contactId)
                || seedMovementRepository.existsBySupplierId(contactId);
    }
}
