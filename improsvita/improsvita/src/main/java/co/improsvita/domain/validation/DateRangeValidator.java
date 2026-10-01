package co.improsvita.domain.validation;

import co.improsvita.domain.exception.InvalidDateRangeException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Componente reutilizable para validar pares de fechas "inicio / fin".
 * <p>
 * Pensado para siembras (inicio de siembra / cosecha estimada), pero no conoce ninguna
 * entidad: cualquier servicio puede usarlo (siembras, plántulas, lotes con
 * ingreso/vencimiento) pasando las fechas y los nombres de campo para el mensaje de error.
 * <p>
 * Reglas:
 * <ol>
 *   <li>Ninguna de las dos fechas puede ser nula.</li>
 *   <li>La fecha final debe ser estrictamente posterior a la inicial.</li>
 * </ol>
 */
@Component
public class DateRangeValidator {

    private static final String START_LABEL_DEFAULT = "fecha de inicio";
    private static final String END_LABEL_DEFAULT = "fecha de cosecha";

    /** Valida un rango de siembra: fecha de inicio y fecha de cosecha. */
    public void validate(LocalDate start, LocalDate end) {
        validate(start, end, START_LABEL_DEFAULT, END_LABEL_DEFAULT);
    }

    /**
     * Valida un rango genérico usando etiquetas propias para los mensajes
     * (ej. "fecha de ingreso" / "fecha de vencimiento").
     */
    public void validate(LocalDate start, LocalDate end, String startLabel, String endLabel) {
        if (start == null) {
            throw new InvalidDateRangeException("La " + startLabel + " es obligatoria");
        }
        if (end == null) {
            throw new InvalidDateRangeException("La " + endLabel + " es obligatoria");
        }
        if (!end.isAfter(start)) {
            throw new InvalidDateRangeException(
                    "La " + endLabel + " (" + end + ") debe ser posterior a la " + startLabel + " (" + start + ")");
        }
    }
}
