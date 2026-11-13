package co.improsvita.assistant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AssistantHistoryMessage(
        @NotBlank
        @Pattern(regexp = "(?i)user|assistant", message = "El rol debe ser user o assistant")
        String role,

        @NotBlank
        @Size(max = 2000, message = "El mensaje del historial no puede superar los 2000 caracteres")
        String content
) {
}
