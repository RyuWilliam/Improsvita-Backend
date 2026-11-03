package co.improsvita.persistence;

import co.improsvita.domain.model.Contact;
import co.improsvita.domain.model.ContactRole;
import co.improsvita.domain.model.SeedSupplier;
import co.improsvita.domain.service.ContactService;
import co.improsvita.domain.service.SeedSupplierService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ContactPersistenceTest {

    @Autowired
    private ContactService contactService;

    @Autowired
    private SeedSupplierService seedSupplierService;

    @Test
    void creaConDosRoles_losPersisteYFiltraPorRol() {
        Contact contact = new Contact();
        contact.setName("Contacto integración");
        contact.setPhone("3009998877");
        contact.setEmail("integracion.contacto@test.local");
        contact.setActive(true);
        contact.setRoles(Set.of(ContactRole.SUPPLIER, ContactRole.CLIENT));

        Contact created = contactService.createContact(contact);

        assertTrue(created.getId() != null);
        assertEquals(Set.of(ContactRole.SUPPLIER, ContactRole.CLIENT),
                contactService.getContactById(created.getId()).getRoles());
        assertEquals(1, contactService.getAllContacts(ContactRole.CLIENT, false)
                .stream()
                .filter(c -> c.getId().equals(created.getId()))
                .count());
    }

    @Test
    void delete_haceBorradoLogicoYExcluyeDelListadoActivo() {
        Contact contact = new Contact();
        contact.setName("Contacto a borrar");
        contact.setPhone("3009998878");
        contact.setEmail("integracion.borrar@test.local");
        contact.setActive(true);
        contact.setRoles(Set.of(ContactRole.EMPLOYEE));

        Contact created = contactService.createContact(contact);
        contactService.deleteContact(created.getId());

        assertFalse(contactService.getContactById(created.getId()).getActive());
        assertTrue(contactService.getAllContacts(ContactRole.EMPLOYEE, false)
                .stream()
                .noneMatch(c -> c.getId().equals(created.getId())));
        assertTrue(contactService.getAllContacts(ContactRole.EMPLOYEE, true)
                .stream()
                .anyMatch(c -> c.getId().equals(created.getId())));
    }

    @Test
    void link_rechazaContactoSinRolSupplier() {
        Contact client = new Contact();
        client.setName("Cliente sin rol proveedor");
        client.setPhone("3009998879");
        client.setEmail("integracion.cliente@test.local");
        client.setActive(true);
        client.setRoles(Set.of(ContactRole.CLIENT));

        Contact created = contactService.createContact(client);

        assertThrows(IllegalArgumentException.class,
                () -> seedSupplierService.link(1, created.getId()));
    }

    @Test
    void link_creaRelacionParaContactoConRolSupplier() {
        Contact supplier = new Contact();
        supplier.setName("Proveedor integración link");
        supplier.setPhone("3009998880");
        supplier.setEmail("integracion.supplier.link@test.local");
        supplier.setActive(true);
        supplier.setRoles(Set.of(ContactRole.SUPPLIER));

        Contact created = contactService.createContact(supplier);
        SeedSupplier link = seedSupplierService.link(1, created.getId());

        assertTrue(link.getId() != null);
        assertTrue(seedSupplierService.getBySupplierId(created.getId())
                .stream()
                .anyMatch(l -> l.getId().equals(link.getId())));
    }
}
