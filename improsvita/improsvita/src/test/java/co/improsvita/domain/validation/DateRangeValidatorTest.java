package co.improsvita.domain.validation;

import co.improsvita.domain.exception.InvalidDateRangeException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DateRangeValidatorTest {

    private final DateRangeValidator validator = new DateRangeValidator();
    private final LocalDate start = LocalDate.of(2026, 10, 1);

    @Test
    void validRange_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validate(start, start.plusDays(60)));
    }

    @Test
    void harvestOneDayAfterStart_isValid() {
        assertDoesNotThrow(() -> validator.validate(start, start.plusDays(1)));
    }

    @Test
    void nullStart_throws() {
        var ex = assertThrows(InvalidDateRangeException.class,
                () -> validator.validate(null, start));
        assertEquals("La fecha de inicio es obligatoria", ex.getMessage());
    }

    @Test
    void nullEnd_throws() {
        var ex = assertThrows(InvalidDateRangeException.class,
                () -> validator.validate(start, null));
        assertEquals("La fecha de cosecha es obligatoria", ex.getMessage());
    }

    @Test
    void harvestBeforeStart_throws() {
        assertThrows(InvalidDateRangeException.class,
                () -> validator.validate(start, start.minusDays(1)));
    }

    @Test
    void harvestSameDayAsStart_throws() {
        assertThrows(InvalidDateRangeException.class,
                () -> validator.validate(start, start));
    }

    @Test
    void customLabels_appearInMessage() {
        var ex = assertThrows(InvalidDateRangeException.class,
                () -> validator.validate(start, start.minusDays(5), "fecha de ingreso", "fecha de vencimiento"));
        assertTrue(ex.getMessage().contains("fecha de vencimiento"));
        assertTrue(ex.getMessage().contains("fecha de ingreso"));
    }

    @Test
    void optionalEnd_nullEnd_isValid() {
        assertDoesNotThrow(() -> validator.validateOptionalEnd(start, null, "fecha de siembra", "fecha de germinación"));
    }

    @Test
    void optionalEnd_nullStart_throws() {
        assertThrows(InvalidDateRangeException.class,
                () -> validator.validateOptionalEnd(null, start, "fecha de siembra", "fecha de germinación"));
    }

    @Test
    void optionalEnd_endBeforeStart_throws() {
        assertThrows(InvalidDateRangeException.class,
                () -> validator.validateOptionalEnd(start, start.minusDays(1), "fecha de siembra", "fecha de germinación"));
    }

    @Test
    void optionalEnd_validEnd_isValid() {
        assertDoesNotThrow(() -> validator.validateOptionalEnd(start, start.plusDays(10), "fecha de siembra", "fecha de germinación"));
    }

    @Test
    void exception_isIllegalArgument_soControllersReturn400WithMessage() {
        var ex = assertThrows(IllegalArgumentException.class, () -> validator.validate(start, start));
        assertInstanceOf(InvalidDateRangeException.class, ex);
    }
}
