package co.improsvita.assistant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AssistantControllerTest {

    @Mock
    private AssistantService assistantService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AssistantController(assistantService))
                .build();
    }

    @Test
    void chatDevuelveRespuestaDelServicio() throws Exception {
        when(assistantService.chat(any(AssistantRequest.class)))
                .thenReturn(new AssistantResponse(
                        "Hay 100 plántulas disponibles.",
                        List.of("disponibilidad"),
                        true,
                        false));

        mockMvc.perform(post("/assistant/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"message":"¿Cuántas plántulas hay?","history":[]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("Hay 100 plántulas disponibles."))
                .andExpect(jsonPath("$.toolsUsed[0]").value("disponibilidad"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.degraded").value(false));
    }

    @Test
    void chatRechazaPreguntaVacia() throws Exception {
        mockMvc.perform(post("/assistant/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"message":"   ","history":[]}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void healthDevuelveEstadoDelAsistente() throws Exception {
        when(assistantService.health())
                .thenReturn(new AssistantHealthResponse(
                        true,
                        false,
                        "Groq",
                        "openai/gpt-oss-20b",
                        "Falta configurar GROQ_API_KEY en el entorno."));

        mockMvc.perform(get("/assistant/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.configured").value(false))
                .andExpect(jsonPath("$.provider").value("Groq"))
                .andExpect(jsonPath("$.model").value("openai/gpt-oss-20b"));
    }
}
