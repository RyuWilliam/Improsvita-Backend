package co.improsvita.assistant;

import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class AssistantToolCallTracker {

    private final ThreadLocal<Set<String>> calls = ThreadLocal.withInitial(LinkedHashSet::new);

    public void start() {
        calls.get().clear();
    }

    public void record(String toolName) {
        calls.get().add(toolName);
    }

    public List<String> finish() {
        List<String> result = List.copyOf(calls.get());
        calls.remove();
        return result;
    }
}
