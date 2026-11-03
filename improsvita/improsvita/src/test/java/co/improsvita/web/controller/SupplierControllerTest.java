package co.improsvita.web.controller;

import co.improsvita.domain.model.Contact;
import co.improsvita.domain.model.ContactRole;
import co.improsvita.domain.service.ContactNotFoundException;
import co.improsvita.domain.service.ContactService;
import co.improsvita.domain.service.DuplicateEmailException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SupplierControllerTest {

    @Mock
    private ContactService contactService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new SupplierController(contactService))
                .build();
    }

    private Contact contact(Integer id, String email) {
        Contact contact = new Contact();
        contact.setId(id);
        contact.setName("Proveedor");
        contact.setPhone("3000000000");
        contact.setEmail(email);
        contact.setActive(true);
        contact.setRoles(Set.of(ContactRole.SUPPLIER));
        return contact;
    }

    @Test
    void getSuppliers_sigueFuncionando() throws Exception {
        when(contactService.getAllContacts(ContactRole.SUPPLIER, true))
                .thenReturn(List.of(contact(1, "proveedor@test.com")));

        mockMvc.perform(get("/suppliers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Proveedor"))
                .andExpect(jsonPath("$[0].email").value("proveedor@test.com"));
    }

    @Test
    void postSupplier_sigueFuncionando() throws Exception {
        when(contactService.createContact(any(Contact.class))).thenAnswer(invocation -> {
            Contact contact = invocation.getArgument(0);
            contact.setId(7);
            return contact;
        });

        String body = """
                {
                  "name": "Nuevo Proveedor",
                  "phone": "3001112233",
                  "email": "nuevo@test.com"
                }
                """;

        mockMvc.perform(post("/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.email").value("nuevo@test.com"));
    }

    @Test
    void deleteSupplier_sigueFuncionando() throws Exception {
        when(contactService.getContactById(7)).thenReturn(contact(7, "proveedor@test.com"));

        mockMvc.perform(delete("/suppliers/7"))
                .andExpect(status().isNoContent());

        verify(contactService).deleteContact(7);
    }

    @Test
    void getSupplierById_soloDevuelveContactosSupplier() throws Exception {
        when(contactService.getContactById(1)).thenReturn(contact(1, "proveedor@test.com"));

        mockMvc.perform(get("/suppliers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("proveedor@test.com"));
    }

    @Test
    void getSupplierById_sinRolSupplier_devuelve404() throws Exception {
        Contact cliente = contact(2, "cliente@test.com");
        cliente.setRoles(Set.of(ContactRole.CLIENT));
        when(contactService.getContactById(2)).thenReturn(cliente);

        mockMvc.perform(get("/suppliers/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSupplierById_inexistente_devuelve404() throws Exception {
        when(contactService.getContactById(99)).thenThrow(new ContactNotFoundException(99));

        mockMvc.perform(get("/suppliers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSupplierByName_devuelveContactoSupplier() throws Exception {
        when(contactService.getContactByName("Proveedor"))
                .thenReturn(contact(1, "proveedor@test.com"));

        mockMvc.perform(get("/suppliers/name/Proveedor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Proveedor"));
    }

    @Test
    void putSupplier_actualizaContacto() throws Exception {
        when(contactService.getContactById(7)).thenReturn(contact(7, "proveedor@test.com"));
        when(contactService.updateContact(eq(7), any(Contact.class))).thenAnswer(invocation -> {
            Contact contact = invocation.getArgument(1);
            contact.setId(7);
            return contact;
        });

        String body = """
                {
                  "name": "Proveedor Actualizado",
                  "phone": "3001112233",
                  "email": "proveedor.actualizado@test.com"
                }
                """;

        mockMvc.perform(put("/suppliers/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("Proveedor Actualizado"));
    }

    @Test
    void postSupplier_emailDuplicado_devuelve409() throws Exception {
        when(contactService.createContact(any(Contact.class)))
                .thenThrow(new DuplicateEmailException("duplicado@test.com"));

        String body = """
                {
                  "name": "Duplicado",
                  "phone": "3001112233",
                  "email": "duplicado@test.com"
                }
                """;

        mockMvc.perform(post("/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteSupplierByName_sigueFuncionando() throws Exception {
        when(contactService.getContactByName("Proveedor"))
                .thenReturn(contact(7, "proveedor@test.com"));

        mockMvc.perform(delete("/suppliers/name/Proveedor"))
                .andExpect(status().isNoContent());

        verify(contactService).deleteContact(7);
    }
}
