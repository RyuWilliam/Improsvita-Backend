package co.improsvita.web.controller;

import co.improsvita.domain.model.Contact;
import co.improsvita.domain.model.ContactRole;
import co.improsvita.domain.service.ContactNotFoundException;
import co.improsvita.domain.service.ContactRoleInUseException;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
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
class ContactControllerTest {

    @Mock
    private ContactService contactService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ContactController(contactService))
                .build();
    }

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
    void getContacts_porRol_devuelveFiltrados() throws Exception {
        when(contactService.getAllContacts(ContactRole.SUPPLIER, false))
                .thenReturn(List.of(contact(1, "proveedor@test.com", Set.of(ContactRole.SUPPLIER))));

        mockMvc.perform(get("/contacts").param("role", "SUPPLIER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("proveedor@test.com"))
                .andExpect(jsonPath("$[0].roles", hasItem("SUPPLIER")));
    }

    @Test
    void postContact_conDosRoles_devuelveContactoConRoles() throws Exception {
        when(contactService.createContact(any(Contact.class))).thenAnswer(invocation -> {
            Contact contact = invocation.getArgument(0);
            contact.setId(10);
            return contact;
        });

        String body = """
                {
                  "name": "Cliente Proveedor",
                  "phone": "3001112233",
                  "email": "cliente.proveedor@test.com",
                  "roles": ["SUPPLIER", "CLIENT"]
                }
                """;

        mockMvc.perform(post("/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.roles", hasItem("SUPPLIER")))
                .andExpect(jsonPath("$.roles", hasItem("CLIENT")));
    }

    @Test
    void postContact_emailDuplicado_devuelve409() throws Exception {
        when(contactService.createContact(any(Contact.class)))
                .thenThrow(new DuplicateEmailException("duplicado@test.com"));

        String body = """
                {
                  "name": "Duplicado",
                  "phone": "3001112233",
                  "email": "duplicado@test.com",
                  "roles": ["SUPPLIER"]
                }
                """;

        mockMvc.perform(post("/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("duplicado@test.com")));
    }

    @Test
    void putContact_quitarRolConReferencias_devuelve409() throws Exception {
        when(contactService.updateContact(eq(1), any(Contact.class)))
                .thenThrow(new ContactRoleInUseException("No se puede quitar el rol SUPPLIER"));

        String body = """
                {
                  "name": "Proveedor",
                  "phone": "3001112233",
                  "email": "proveedor@test.com",
                  "roles": ["CLIENT"]
                }
                """;

        mockMvc.perform(put("/contacts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("SUPPLIER")));
    }

    @Test
    void deleteContact_devuelve204() throws Exception {
        mockMvc.perform(delete("/contacts/1"))
                .andExpect(status().isNoContent());

        verify(contactService).deleteContact(1);
    }

    @Test
    void getContact_inexistente_devuelve404() throws Exception {
        when(contactService.getContactById(99)).thenThrow(new ContactNotFoundException(99));

        mockMvc.perform(get("/contacts/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getContacts_sinFiltro_usaIncludeInactivePorDefecto() throws Exception {
        when(contactService.getAllContacts(null, false))
                .thenReturn(List.of(contact(1, "contacto@test.com", Set.of(ContactRole.EMPLOYEE))));

        mockMvc.perform(get("/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].roles", hasItem("EMPLOYEE")));
    }

    @Test
    void putContact_actualizaCorrectamente() throws Exception {
        when(contactService.updateContact(eq(1), any(Contact.class))).thenAnswer(invocation -> {
            Contact contact = invocation.getArgument(1);
            contact.setId(1);
            return contact;
        });

        String body = """
                {
                  "name": "Proveedor Actualizado",
                  "phone": "3001112244",
                  "email": "proveedor.actualizado@test.com",
                  "roles": ["SUPPLIER"]
                }
                """;

        mockMvc.perform(put("/contacts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Proveedor Actualizado"))
                .andExpect(jsonPath("$.email").value("proveedor.actualizado@test.com"));
    }

    @Test
    void postContact_sinRoles_devuelve400() throws Exception {
        String body = """
                {
                  "name": "Sin roles",
                  "phone": "3001112233",
                  "email": "sin.roles@test.com",
                  "roles": []
                }
                """;

        mockMvc.perform(post("/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
