package co.improsvita.web.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * Red de seguridad global para que ningún error del backend se pierda.
 *
 * Antes de existir este advice, cualquier excepción no capturada terminaba en el
 * error dispatch de /error y, con /error no público, el cliente veía un 403 vacío
 * en lugar del 400/404/405/409/500 real. Los controladores siguen capturando lo que
 * ya capturaban; esto solo cubre lo que se escapaba.
 *
 * El cuerpo de la respuesta se mantiene en texto plano, igual que el resto de los
 * mensajes de error del proyecto.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgument(IllegalArgumentException ex) {
        return messageOr(ex.getMessage(), "Solicitud inválida");
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleIllegalState(IllegalStateException ex) {
        return messageOr(ex.getMessage(), "Operación no permitida en el estado actual");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Violación de integridad de datos", ex);
        return "El registro viola una restricción de la base de datos "
                + "(valor duplicado en un campo único o campo obligatorio vacío)";
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleUnreadableBody(HttpMessageNotReadableException ex) {
        return "Cuerpo de la petición inválido o malformado";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBeanValidation(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleConstraintViolation(ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMissingParameter(MissingServletRequestParameterException ex) {
        return "Falta el parámetro obligatorio: " + ex.getParameterName();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return "Valor inválido para el parámetro " + ex.getName() + ": " + ex.getValue();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String allowed = ex.getSupportedHttpMethods() == null
                ? ""
                : ex.getSupportedHttpMethods().stream()
                        .map(HttpMethod::name)
                        .sorted()
                        .collect(Collectors.joining(", "));

        ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED);
        if (!allowed.isEmpty()) {
            builder.header(HttpHeaders.ALLOW, allowed);
        }
        return builder.body("Método " + ex.getMethod() + " no permitido en esta ruta."
                + (allowed.isEmpty() ? "" : " Permitidos: " + allowed));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResource(NoResourceFoundException ex) {
        return "Ruta no encontrada: /" + ex.getResourcePath();
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<String> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        return plainText(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Content-Type no soportado: " + ex.getContentType() + ". Usa application/json");
    }

    /**
     * El cliente no acepta text/plain (que es lo único que producimos). Se fija el
     * Content-Type a mano para que Spring no reintente negociar y se genere un bucle.
     */
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<String> handleNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
        return plainText(HttpStatus.NOT_ACCEPTABLE,
                "No puedo responder en los formatos que acepta el cliente. Este API solo produce text/plain y application/json");
    }

    private ResponseEntity<String> plainText(HttpStatus status, String body) {
        return ResponseEntity.status(status)
                .contentType(MediaType.TEXT_PLAIN)
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUnexpected(Exception ex) {
        log.error("Error no controlado", ex);
        return "Error interno del servidor: " + ex.getClass().getSimpleName();
    }

    private String messageOr(String message, String fallback) {
        return (message == null || message.isBlank()) ? fallback : message;
    }
}
