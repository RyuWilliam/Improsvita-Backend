package co.improsvita.assistant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AssistantRequest(
        @NotBlank(message = "La pregunta es obligatoria")
        @Size(max = 1000, message = "La pregunta no puede superar los 1000 caracteres")
        String message,

        @Size(max = 8, message = "El historial no puede superar los 8 mensajes")
        List<@Valid AssistantHistoryMessage> history
) {
}
