package co.improsvita.web.controller;

import co.improsvita.domain.model.Contact;
import co.improsvita.domain.service.ContactService;
import co.improsvita.web.dto.ContactRequest;
import co.improsvita.web.dto.ContactResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
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
    public ResponseEntity<List<ContactResponse>> getAllContacts() {
        List<ContactResponse> contacts = contactService.getAllContacts()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponse> getContactById(@PathVariable Integer id) {
        Contact contact = contactService.getContactById(id);
        if (contact == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(contact));
    }

    @GetMapping("/document/{documentNumber}")
    public ResponseEntity<ContactResponse> getContactByDocumentNumber(@PathVariable String documentNumber) {
        try {
            Contact contact = contactService.getContactByDocumentNumber(documentNumber);
            if (contact == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(toResponse(contact));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createContact(@Valid @RequestBody ContactRequest request) {
        try {
            Contact created = contactService.createContact(toContact(request));
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateContact(@PathVariable Integer id, @Valid @RequestBody ContactRequest request) {
        if (contactService.getContactById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            Contact updated = contactService.updateContact(id, toContact(request));
            return ResponseEntity.ok(toResponse(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Integer id) {
        if (contactService.getContactById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }

    private Contact toContact(ContactRequest request) {
        Contact contact = new Contact();
        contact.setName(request.getName());
        contact.setDocumentType(request.getDocumentType());
        contact.setDocumentNumber(request.getDocumentNumber());
        contact.setPhone(request.getPhone());
        contact.setEmail(request.getEmail());
        contact.setAddress(request.getAddress());
        contact.setActive(request.getActive());
        return contact;
    }

    private ContactResponse toResponse(Contact contact) {
        return new ContactResponse(
                contact.getId(),
                contact.getName(),
                contact.getDocumentType(),
                contact.getDocumentNumber(),
                contact.getPhone(),
                contact.getEmail(),
                contact.getAddress(),
                contact.getActive()
        );
    }

    private Map<String, String> error(String message) {
        return Map.of("message", message);
    }
}
