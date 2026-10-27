package co.improsvita.domain.service;

import co.improsvita.domain.model.Contact;
import co.improsvita.domain.repository.ContactRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @InjectMocks
    private ContactService contactService;

    private Contact contact(Integer id, String documentNumber, String email) {
        Contact contact = new Contact();
        contact.setId(id);
        contact.setName("Juan Pérez");
        contact.setDocumentType("cc");
        contact.setDocumentNumber(documentNumber);
        contact.setPhone("3201234567");
        contact.setEmail(email);
        contact.setAddress("Calle 10 # 5-20");
        return contact;
    }

    @Test
    void create_normalizaCamposYActivaPorDefecto() {
        when(contactRepository.getByDocumentNumber("1020304050")).thenReturn(null);
        when(contactRepository.getByEmail("juan.perez@example.com")).thenReturn(null);
        when(contactRepository.save(any(Contact.class))).thenAnswer(i -> i.getArgument(0));

        Contact created = contactService.createContact(
                contact(null, "  1020304050  ", "  Juan.Perez@Example.com  "));

        assertEquals("1020304050", created.getDocumentNumber());
        assertEquals("CC", created.getDocumentType());
        assertEquals("juan.perez@example.com", created.getEmail());
        assertTrue(created.getActive());
    }

    @Test
    void create_nombreVacio_lanzaIllegalArgument() {
        Contact contact = contact(null, "1020304050", null);
        contact.setName("   ");

        assertThrows(IllegalArgumentException.class, () -> contactService.createContact(contact));
        verify(contactRepository, never()).save(any());
    }

    @Test
    void create_documentoDuplicado_lanzaIllegalState() {
        when(contactRepository.getByDocumentNumber("1020304050"))
                .thenReturn(contact(1, "1020304050", null));

        assertThrows(IllegalStateException.class,
                () -> contactService.createContact(contact(null, "1020304050", null)));
        verify(contactRepository, never()).save(any());
    }

    @Test
    void create_correoDuplicado_lanzaIllegalState() {
        when(contactRepository.getByDocumentNumber("1020304050")).thenReturn(null);
        when(contactRepository.getByEmail("juan.perez@example.com"))
                .thenReturn(contact(1, "999", "juan.perez@example.com"));

        assertThrows(IllegalStateException.class,
                () -> contactService.createContact(contact(null, "1020304050", "juan.perez@example.com")));
        verify(contactRepository, never()).save(any());
    }

    @Test
    void update_conservaSuPropioDocumentoYCorreo() {
        when(contactRepository.getById(1)).thenReturn(contact(1, "1020304050", "juan.perez@example.com"));
        when(contactRepository.getByDocumentNumber("1020304050"))
                .thenReturn(contact(1, "1020304050", "juan.perez@example.com"));
        when(contactRepository.getByEmail("juan.perez@example.com"))
                .thenReturn(contact(1, "1020304050", "juan.perez@example.com"));
        when(contactRepository.save(any(Contact.class))).thenAnswer(i -> i.getArgument(0));

        Contact changes = contact(1, "1020304050", "juan.perez@example.com");
        changes.setPhone("3000000000");
        changes.setActive(false);

        Contact updated = contactService.updateContact(1, changes);

        assertEquals("3000000000", updated.getPhone());
        assertFalse(updated.getActive());
    }

    @Test
    void update_documentoDeOtroContacto_lanzaIllegalState() {
        when(contactRepository.getById(1)).thenReturn(contact(1, "1020304050", null));
        when(contactRepository.getByDocumentNumber("999")).thenReturn(contact(2, "999", null));

        assertThrows(IllegalStateException.class,
                () -> contactService.updateContact(1, contact(1, "999", null)));
        verify(contactRepository, never()).save(any());
    }

    @Test
    void update_inexistente_lanzaIllegalArgument() {
        when(contactRepository.getById(99)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> contactService.updateContact(99, contact(99, "1020304050", null)));
        verify(contactRepository, never()).save(any());
    }

    @Test
    void delete_existente_borra() {
        when(contactRepository.getById(5)).thenReturn(contact(5, "1020304050", null));

        contactService.deleteContact(5);

        verify(contactRepository).deleteById(5);
    }

    @Test
    void delete_inexistente_lanzaIllegalArgumentYNoBorra() {
        when(contactRepository.getById(99)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> contactService.deleteContact(99));
        verify(contactRepository, never()).deleteById(any());
    }

    @Test
    void getByDocumentNumber_trimYVacio() {
        when(contactRepository.getByDocumentNumber("1020304050"))
                .thenReturn(contact(1, "1020304050", null));

        assertEquals(1, contactService.getContactByDocumentNumber("  1020304050  ").getId());
        assertThrows(IllegalArgumentException.class, () -> contactService.getContactByDocumentNumber("  "));
        assertNull(contactService.getContactByDocumentNumber("000"));
    }
}
