package co.improsvita.web.controller;

import co.improsvita.domain.model.Contact;
import co.improsvita.domain.model.ContactRole;
import co.improsvita.domain.service.ContactNotFoundException;
import co.improsvita.domain.service.ContactRoleInUseException;
import co.improsvita.domain.service.ContactService;
import co.improsvita.domain.service.DuplicateEmailException;
import co.improsvita.web.dto.SupplierRequest;
import co.improsvita.web.dto.SupplierResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @deprecated usar {@link ContactController} en /contacts. Se conserva como alias
 * de los contactos con rol SUPPLIER para no romper al frontend.
 */
@Deprecated
@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    private final ContactService contactService;

    public SupplierController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public ResponseEntity<List<SupplierResponse>> getAllSuppliers() {
        List<SupplierResponse> suppliers = contactService
                .getAllContacts(ContactRole.SUPPLIER, true)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponse> getSupplierById(@PathVariable Integer id) {
        Contact contact = findSupplierById(id);
        if (contact == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(contact));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<SupplierResponse> getSupplierByName(@PathVariable String name) {
        Contact contact = contactService.getContactByName(name);
        if (contact == null || !contact.getRoles().contains(ContactRole.SUPPLIER)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(contact));
    }

    @PostMapping
    public ResponseEntity<?> createSupplier(@Valid @RequestBody SupplierRequest request) {
        Contact contact = new Contact();
        contact.setName(request.getName());
        contact.setPhone(request.getPhone());
        contact.setEmail(request.getEmail());
        contact.setActive(true);
        contact.setRoles(Set.of(ContactRole.SUPPLIER));
        try {
            Contact created = contactService.createContact(contact);
            return ResponseEntity.ok(toResponse(created));
        } catch (DuplicateEmailException e) {
            return conflict(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSupplier(@PathVariable Integer id,
                                            @Valid @RequestBody SupplierRequest request) {
        Contact existing = findSupplierById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setName(request.getName());
        existing.setPhone(request.getPhone());
        existing.setEmail(request.getEmail());

        try {
            Contact updated = contactService.updateContact(id, existing);
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
    public ResponseEntity<Void> deleteSupplierById(@PathVariable Integer id) {
        Contact contact = findSupplierById(id);
        if (contact != null) {
            contactService.deleteContact(id);
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/name/{name}")
    public ResponseEntity<Void> deleteSupplierByName(@PathVariable String name) {
        Contact contact = contactService.getContactByName(name);
        if (contact != null && contact.getRoles().contains(ContactRole.SUPPLIER)) {
            contactService.deleteContact(contact.getId());
        }
        return ResponseEntity.noContent().build();
    }

    private Contact findSupplierById(Integer id) {
        Contact contact;
        try {
            contact = contactService.getContactById(id);
        } catch (ContactNotFoundException e) {
            return null;
        }
        return contact.getRoles().contains(ContactRole.SUPPLIER) ? contact : null;
    }

    private SupplierResponse toResponse(Contact contact) {
        SupplierResponse response = new SupplierResponse();
        response.setId(contact.getId());
        response.setName(contact.getName());
        response.setPhone(contact.getPhone());
        response.setEmail(contact.getEmail());
        return response;
    }

    private ResponseEntity<Map<String, String>> conflict(String message) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", message));
    }
}
