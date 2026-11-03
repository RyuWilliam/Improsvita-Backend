package co.improsvita.domain.service;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("Ya existe un contacto con el email: " + email);
    }
}
