package co.improsvita.domain.service;

public class ContactRoleInUseException extends RuntimeException {

    public ContactRoleInUseException(String message) {
        super(message);
    }
}
