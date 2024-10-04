package org.chunsik.pq.config;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.generate.manager.AIManager;
import org.chunsik.pq.generate.manager.OpenAIManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AIConfig {

    private final OpenAIManager openAIManager;


    @Bean
    public AIManager aiManager() {
        return openAIManager;
    }
}