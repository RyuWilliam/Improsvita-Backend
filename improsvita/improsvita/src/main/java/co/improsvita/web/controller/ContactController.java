package co.improsvita.web.controller;

import co.improsvita.domain.model.Contact;
import co.improsvita.domain.model.ContactRole;
import co.improsvita.domain.service.ContactNotFoundException;
import co.improsvita.domain.service.ContactRoleInUseException;
import co.improsvita.domain.service.ContactService;
import co.improsvita.domain.service.DuplicateEmailException;
import co.improsvita.web.dto.ContactRequest;
import co.improsvita.web.dto.ContactResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public ResponseEntity<List<ContactResponse>> getAllContacts(
            @RequestParam(required = false) ContactRole role,
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        List<ContactResponse> contacts = contactService.getAllContacts(role, includeInactive)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponse> getContactById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(toResponse(contactService.getContactById(id)));
        } catch (ContactNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createContact(@Valid @RequestBody ContactRequest request) {
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
    public ResponseEntity<?> updateContact(@PathVariable Integer id,
                                           @Valid @RequestBody ContactRequest request) {
        try {
            Contact updated = contactService.updateContact(id, toDomain(request));
            return ResponseEntity.ok(toResponse(updated));
        } catch (ContactNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (DuplicateEmailException | ContactRoleInUseException e) {
            return conflict(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Integer id) {
        try {
            contactService.deleteContact(id);
            return ResponseEntity.noContent().build();
        } catch (ContactNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Contact toDomain(ContactRequest request) {
        Contact contact = new Contact();
        contact.setName(request.getName());
        contact.setPhone(request.getPhone());
        contact.setEmail(request.getEmail());
        contact.setRoles(request.getRoles());
        contact.setActive(true);
        return contact;
    }

    private ContactResponse toResponse(Contact contact) {
        ContactResponse response = new ContactResponse();
        response.setId(contact.getId());
        response.setName(contact.getName());
        response.setPhone(contact.getPhone());
        response.setEmail(contact.getEmail());
        response.setActive(contact.getActive());
        response.setRoles(contact.getRoles());
        response.setCreatedDate(contact.getCreatedDate());
        response.setLastUpdated(contact.getLastUpdated());
        return response;
    }

    private ResponseEntity<Map<String, String>> conflict(String message) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", message));
    }
}
