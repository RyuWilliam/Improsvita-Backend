package co.improsvita.assistant;

public record AssistantHealthResponse(
        boolean enabled,
        boolean configured,
        String provider,
        String model,
        String message
) {
}
