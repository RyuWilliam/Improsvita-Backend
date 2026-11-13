package co.improsvita.assistant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssistantServiceTest {

    @Mock
    private ChatClient chatClient;
    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;
    @Mock
    private ChatClient.CallResponseSpec responseSpec;

    private AssistantService service;
    private AssistantToolCallTracker tracker;

    @BeforeEach
    void setUp() {
        tracker = new AssistantToolCallTracker();
        service = new AssistantService(
                chatClient,
                tracker,
                true,
                "Groq",
                "test-key",
                "openai/gpt-oss-20b",
                8,
                1000);
    }

    @Test
    void sinApiKeyDevuelveMensajeDeConfiguracionSinLlamarAlProveedor() {
        AssistantService unconfigured = new AssistantService(
                chatClient,
                tracker,
                true,
                "Groq",
                "not-configured",
                "openai/gpt-oss-20b",
                8,
                1000);

        AssistantResponse response = unconfigured.chat(
                new AssistantRequest("¿Cuántas plántulas hay?", List.of()));

        assertFalse(response.available());
        assertFalse(response.degraded());
        assertTrue(response.reply().contains("GROQ_API_KEY"));
        assertEquals(List.of(), response.toolsUsed());
        verifyNoInteractions(chatClient);
    }

    @Test
    void chatEnviaSystemPromptHistorialYPreguntaActual() {
        when(chatClient.prompt(any(Prompt.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn("Hay 100 plántulas disponibles.");

        AssistantResponse response = service.chat(new AssistantRequest(
                "¿Cuántas plántulas hay?",
                List.of(new AssistantHistoryMessage("user", "Hola"),
                        new AssistantHistoryMessage("assistant", "¿Qué necesitas?"))));

        assertTrue(response.available());
        assertFalse(response.degraded());
        assertEquals("Hay 100 plántulas disponibles.", response.reply());

        ArgumentCaptor<Prompt> promptCaptor = ArgumentCaptor.forClass(Prompt.class);
        verify(chatClient).prompt(promptCaptor.capture());
        assertEquals(4, promptCaptor.getValue().getInstructions().size());
    }

    @Test
    void rechazaPreguntasFueraDelNegocioSinLlamarAlProveedor() {
        List<String> questions = List.of(
                "Genera un hola mundo en Java",
                "Escríbeme un poema",
                "¿Cuál es el clima de mañana?");

        for (String question : questions) {
            AssistantResponse response = service.chat(
                    new AssistantRequest(question, List.of()));

            assertTrue(response.available());
            assertFalse(response.degraded());
            assertTrue(response.reply().contains("Solo puedo ayudarte"));
            assertEquals(List.of(), response.toolsUsed());
        }

        verifyNoInteractions(chatClient);
    }

    @Test
    void permiteSeguimientoCuandoElHistorialEsDelNegocio() {
        when(chatClient.prompt(any(Prompt.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn("Hay 40 disponibles.");

        AssistantResponse response = service.chat(new AssistantRequest(
                "¿Y cuántas hay?",
                List.of(new AssistantHistoryMessage("user", "¿Cuántas plántulas hay?"))));

        assertTrue(response.available());
        assertFalse(response.degraded());
        assertEquals("Hay 40 disponibles.", response.reply());
        verify(chatClient).prompt(any(Prompt.class));
    }

    @Test
    void permiteTemasDelNegocioConPalabrasAmbiguas() {
        when(chatClient.prompt(any(Prompt.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn("Según las herramientas, todo está al día.");

        List<String> questions = List.of(
                "¿Cómo va la programación de siembras?",
                "¿Cuál es la política de precios?",
                "¿Cuántas plántulas cuento?",
                "¿Tenemos lechuga?");

        for (String question : questions) {
            AssistantResponse response = service.chat(
                    new AssistantRequest(question, List.of()));

            assertTrue(response.available());
            assertFalse(response.degraded());
            assertEquals("Según las herramientas, todo está al día.", response.reply());
        }
    }

    @Test
    void ocultaReferenciasTecnicasEnLaRespuesta() {
        when(chatClient.prompt(any(Prompt.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn(
                "Puedo consultarla (con listarStockBajo) desde /assistant/chat.");

        AssistantResponse response = service.chat(
                new AssistantRequest("¿Qué inventario tenemos?", List.of()));

        assertTrue(response.reply().contains("consultarla"));
        assertFalse(response.reply().contains("listarStockBajo"));
        assertFalse(response.reply().contains("/assistant/chat"));
        assertTrue(response.reply().contains("esa consulta"));
    }

    @Test
    void chatTraduceErrorDeLimiteDeCuota() {
        when(chatClient.prompt(any(Prompt.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content())
                .thenThrow(new IllegalStateException("429 rate limit exceeded"));

        AssistantResponse response = service.chat(
                new AssistantRequest("¿Cómo va el vivero?", List.of()));

        assertTrue(response.available());
        assertTrue(response.degraded());
        assertTrue(response.reply().contains("límite"));
    }

    @Test
    void chatValidaPreguntaVacia() {
        assertThrows(IllegalArgumentException.class,
                () -> service.chat(new AssistantRequest("   ", List.of())));
    }

    @Test
    void chatValidaLongitudMaxima() {
        String longMessage = "a".repeat(1001);
        assertThrows(IllegalArgumentException.class,
                () -> service.chat(new AssistantRequest(longMessage, List.of())));
    }

    @Test
    void healthReflejaConfiguracion() {
        AssistantHealthResponse health = service.health();

        assertTrue(health.enabled());
        assertTrue(health.configured());
        assertEquals("Groq", health.provider());
        assertEquals("openai/gpt-oss-20b", health.model());
        assertTrue(health.message().contains("listo"));
    }
}
