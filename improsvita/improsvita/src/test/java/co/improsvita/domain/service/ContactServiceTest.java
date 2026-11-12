package co.improsvita.domain.service;

import co.improsvita.domain.model.Contact;
import co.improsvita.domain.model.ContactRole;
import co.improsvita.domain.model.Reservation;
import co.improsvita.domain.repository.ContactRepository;
import co.improsvita.domain.repository.ReservationRepository;
import co.improsvita.domain.repository.SeedMovementRepository;
import co.improsvita.domain.repository.SeedSupplierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private SeedSupplierRepository seedSupplierRepository;

    @Mock
    private SeedMovementRepository seedMovementRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ContactService contactService;

    private Contact contact(Integer id, String email, Set<ContactRole> roles) {
        Contact contact = new Contact();
        contact.setId(id);
        contact.setName("Contacto");
        contact.setPhone("3000000000");
        contact.setEmail(email);
        contact.setActive(true);
        contact.setRoles(roles);
        return contact;
    }

    @Test
    void create_conDosRoles_guardaAmbosRoles() {
        Contact request = contact(null, "dos@roles.com",
                Set.of(ContactRole.SUPPLIER, ContactRole.CLIENT));
        when(contactRepository.getByEmail("dos@roles.com")).thenReturn(null);
        when(contactRepository.save(any(Contact.class))).thenAnswer(i -> i.getArgument(0));

        Contact created = contactService.createContact(request);

        assertEquals(Set.of(ContactRole.SUPPLIER, ContactRole.CLIENT), created.getRoles());
        verify(contactRepository).save(request);
    }

    @Test
    void create_emailDuplicado_lanzaDuplicateEmailException() {
        Contact request = contact(null, "duplicado@test.com", Set.of(ContactRole.SUPPLIER));
        when(contactRepository.getByEmail("duplicado@test.com"))
                .thenReturn(contact(5, "duplicado@test.com", Set.of(ContactRole.SUPPLIER)));

        assertThrows(DuplicateEmailException.class, () -> contactService.createContact(request));
        verify(contactRepository, never()).save(any());
    }

    @Test
    void create_sinRoles_lanzaIllegalArgument() {
        Contact request = contact(null, "sinroles@test.com", Set.of());

        assertThrows(IllegalArgumentException.class, () -> contactService.createContact(request));
        verify(contactRepository, never()).save(any());
    }

    @Test
    void getAll_pasaFiltroDeRolYIncludeInactive() {
        Contact supplier = contact(1, "proveedor@test.com", Set.of(ContactRole.SUPPLIER));
        when(contactRepository.getAll(ContactRole.SUPPLIER, false)).thenReturn(List.of(supplier));

        List<Contact> result = contactService.getAllContacts(ContactRole.SUPPLIER, false);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getRoles().contains(ContactRole.SUPPLIER));
        verify(contactRepository).getAll(ContactRole.SUPPLIER, false);
    }

    @Test
    void update_quitarSupplierConReferencias_lanzaContactRoleInUseException() {
        Contact existing = contact(1, "proveedor@test.com", Set.of(ContactRole.SUPPLIER));
        Contact update = contact(null, "proveedor@test.com", Set.of(ContactRole.CLIENT));

        when(contactRepository.getById(1)).thenReturn(existing);
        when(contactRepository.getByEmail("proveedor@test.com")).thenReturn(existing);
        when(seedSupplierRepository.existsBySupplierId(1)).thenReturn(true);

        assertThrows(ContactRoleInUseException.class,
                () -> contactService.updateContact(1, update));
        verify(contactRepository, never()).save(any());
    }

    @Test
    void update_quitarSupplierSinReferencias_actualizaRoles() {
        Contact existing = contact(1, "proveedor@test.com", Set.of(ContactRole.SUPPLIER));
        Contact update = contact(null, "proveedor@test.com", Set.of(ContactRole.CLIENT));

        when(contactRepository.getById(1)).thenReturn(existing);
        when(contactRepository.getByEmail("proveedor@test.com")).thenReturn(existing);
        when(seedSupplierRepository.existsBySupplierId(1)).thenReturn(false);
        when(seedMovementRepository.existsBySupplierId(1)).thenReturn(false);
        when(contactRepository.save(any(Contact.class))).thenAnswer(i -> i.getArgument(0));

        Contact updated = contactService.updateContact(1, update);

        assertFalse(updated.getRoles().contains(ContactRole.SUPPLIER));
        assertTrue(updated.getRoles().contains(ContactRole.CLIENT));
    }

    @Test
    void update_quitarClientConReservas_lanzaContactRoleInUseException() {
        Contact existing = contact(1, "cliente@test.com", Set.of(ContactRole.CLIENT));
        Contact update = contact(null, "cliente@test.com", Set.of(ContactRole.SUPPLIER));

        when(contactRepository.getById(1)).thenReturn(existing);
        when(contactRepository.getByEmail("cliente@test.com")).thenReturn(existing);
        when(reservationRepository.getByClientId(1)).thenReturn(List.of(new Reservation()));

        assertThrows(ContactRoleInUseException.class,
                () -> contactService.updateContact(1, update));
        verify(contactRepository, never()).save(any());
    }

    @Test
    void update_quitarClientSinReservas_actualizaRoles() {
        Contact existing = contact(1, "cliente@test.com", Set.of(ContactRole.CLIENT));
        Contact update = contact(null, "cliente@test.com", Set.of(ContactRole.SUPPLIER));

        when(contactRepository.getById(1)).thenReturn(existing);
        when(contactRepository.getByEmail("cliente@test.com")).thenReturn(existing);
        when(reservationRepository.getByClientId(1)).thenReturn(List.of());
        when(contactRepository.save(any(Contact.class))).thenAnswer(i -> i.getArgument(0));

        Contact updated = contactService.updateContact(1, update);

        assertFalse(updated.getRoles().contains(ContactRole.CLIENT));
        assertTrue(updated.getRoles().contains(ContactRole.SUPPLIER));
    }

    @Test
    void update_emailDeOtroContacto_lanzaDuplicateEmailException() {
        Contact existing = contact(1, "propio@test.com", Set.of(ContactRole.SUPPLIER));
        Contact otro = contact(2, "otro@test.com", Set.of(ContactRole.CLIENT));
        Contact update = contact(null, "otro@test.com", Set.of(ContactRole.SUPPLIER));

        when(contactRepository.getById(1)).thenReturn(existing);
        when(contactRepository.getByEmail("otro@test.com")).thenReturn(otro);

        assertThrows(DuplicateEmailException.class,
                () -> contactService.updateContact(1, update));
        verify(contactRepository, never()).save(any());
    }

    @Test
    void delete_haceBorradoLogico() {
        Contact existing = contact(1, "borrar@test.com", Set.of(ContactRole.SUPPLIER));
        when(contactRepository.getById(1)).thenReturn(existing);
        when(contactRepository.save(any(Contact.class))).thenAnswer(i -> i.getArgument(0));

        contactService.deleteContact(1);

        assertFalse(existing.getActive());
        verify(contactRepository).save(existing);
    }

    @Test
    void getById_inexistente_lanzaContactNotFoundException() {
        when(contactRepository.getById(99)).thenReturn(null);

        assertThrows(ContactNotFoundException.class, () -> contactService.getContactById(99));
    }

    @Test
    void assertHasRole_conRol_devuelveContacto() {
        Contact supplier = contact(1, "proveedor@test.com", Set.of(ContactRole.SUPPLIER));
        when(contactRepository.getById(1)).thenReturn(supplier);

        Contact result = contactService.assertHasRole(1, ContactRole.SUPPLIER);

        assertEquals(supplier, result);
    }

    @Test
    void assertHasRole_sinRol_lanzaIllegalArgument() {
        Contact client = contact(1, "cliente@test.com", Set.of(ContactRole.CLIENT));
        when(contactRepository.getById(1)).thenReturn(client);

        assertThrows(IllegalArgumentException.class,
                () -> contactService.assertHasRole(1, ContactRole.SUPPLIER));
    }

    @Test
    void assertHasRole_inexistente_lanzaContactNotFoundException() {
        when(contactRepository.getById(99)).thenReturn(null);

        assertThrows(ContactNotFoundException.class,
                () -> contactService.assertHasRole(99, ContactRole.SUPPLIER));
    }
}
