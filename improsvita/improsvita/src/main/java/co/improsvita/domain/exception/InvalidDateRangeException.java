package co.improsvita.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Se lanza cuando un rango de fechas (inicio/cosecha) no cumple las reglas de negocio.
 * <p>
 * Hereda de {@link IllegalArgumentException} para que los controladores que ya capturan esa
 * excepción (ej. SowingController) respondan 400 con el mensaje. {@code @ResponseStatus} cubre
 * los controladores que no la capturan, mientras no exista el manejador global (Sprint 4).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDateRangeException extends IllegalArgumentException {

    public InvalidDateRangeException(String message) {
        super(message);
    }
}
