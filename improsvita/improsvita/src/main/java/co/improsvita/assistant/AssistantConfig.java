package co.improsvita.assistant;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AssistantConfig {

    @Bean
    public ChatClient assistantChatClient(ChatClient.Builder builder, AssistantTools tools) {
        return builder
                .defaultTools(tools)
                .build();
    }
}
