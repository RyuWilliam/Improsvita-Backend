package co.improsvita.domain.service;

public class ContactNotFoundException extends RuntimeException {

    public ContactNotFoundException(Integer id) {
        super("Contacto no encontrado: " + id);
    }

    public ContactNotFoundException(String message) {
        super(message);
    }
}
