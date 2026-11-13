package co.improsvita.assistant;

import java.util.List;

public record AssistantResponse(
        String reply,
        List<String> toolsUsed,
        boolean available,
        boolean degraded
) {
}
