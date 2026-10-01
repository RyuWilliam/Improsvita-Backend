package co.improsvita.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Se lanza cuando un rango de fechas (inicio/cosecha) no cumple las reglas de negocio.
 * <p>
 * {@code @ResponseStatus} hace que, mientras no exista el manejador global de excepciones
 * (tarea del Sprint 4), el cliente reciba un 400 con el mensaje en lugar de un 500.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDateRangeException extends RuntimeException {

    public InvalidDateRangeException(String message) {
        super(message);
    }
}
