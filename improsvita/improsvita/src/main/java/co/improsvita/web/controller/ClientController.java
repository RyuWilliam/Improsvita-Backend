package co.improsvita.web.controller;

import co.improsvita.domain.model.Contact;
import co.improsvita.domain.model.ContactRole;
import co.improsvita.domain.service.ContactNotFoundException;
import co.improsvita.domain.service.ContactRoleInUseException;
import co.improsvita.domain.service.ContactService;
import co.improsvita.domain.service.DuplicateEmailException;
import co.improsvita.web.dto.ClientRequest;
import co.improsvita.web.dto.ClientResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ContactService contactService;

    public ClientController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> getAllClients() {
        List<ClientResponse> clients = contactService.getAllContacts(ContactRole.CLIENT, false)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(toResponse(contactService.assertHasRole(id, ContactRole.CLIENT)));
        } catch (ContactNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ClientResponse> getClientByName(@PathVariable String name) {
        Contact contact = contactService.getContactByName(name);
        if (contact == null || !contact.getRoles().contains(ContactRole.CLIENT)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(contact));
    }

    @PostMapping
    public ResponseEntity<?> createClient(@Valid @RequestBody ClientRequest request) {
        try {
            Contact created = contactService.createContact(toDomain(request));
            return ResponseEntity.ok(toResponse(created));
        } catch (DuplicateEmailException e) {
            return conflict(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateClient(@PathVariable Integer id, @Valid @RequestBody ClientRequest request) {
        Contact existing;
        try {
            existing = contactService.assertHasRole(id, ContactRole.CLIENT);
        } catch (ContactNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }

        existing.setName(request.getName());
        existing.setPhone(request.getPhone());
        existing.setEmail(request.getEmail());

        try {
            Contact updated = contactService.updateContact(id, existing);
            return ResponseEntity.ok(toResponse(updated));
        } catch (DuplicateEmailException | ContactRoleInUseException e) {
            return conflict(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClientById(@PathVariable Integer id) {
        try {
            contactService.assertHasRole(id, ContactRole.CLIENT);
            contactService.deleteContact(id);
            return ResponseEntity.noContent().build();
        } catch (ContactNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Contact toDomain(ClientRequest request) {
        Contact contact = new Contact();
        contact.setName(request.getName());
        contact.setPhone(request.getPhone());
        contact.setEmail(request.getEmail());
        contact.setActive(true);
        contact.setRoles(EnumSet.of(ContactRole.CLIENT));
        return contact;
    }

    private ClientResponse toResponse(Contact contact) {
        ClientResponse response = new ClientResponse();
        response.setId(contact.getId());
        response.setName(contact.getName());
        response.setPhone(contact.getPhone());
        response.setEmail(contact.getEmail());
        response.setActive(contact.getActive());
        return response;
    }

    private ResponseEntity<Map<String, String>> conflict(String message) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", message));
    }
}
