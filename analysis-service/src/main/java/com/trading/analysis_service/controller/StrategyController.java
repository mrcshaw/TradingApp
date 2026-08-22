package com.trading.analysis_service.controller;

import com.trading.analysis_service.dto.StrategyGenerationRequest;
import com.trading.analysis_service.dto.StrategyScriptResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.ParameterizedTypeReference;

@RestController
@RequestMapping("/api/analysis")
public class StrategyController {

    private final ChatClient chatClient;

    public StrategyController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
            .defaultSystem("You are a quantitative trading developer. " +
                           "Convert the user's natural language trading strategy into a strict JSON representation.")
            .build();
    }

    @PostMapping("/strategy/generate")
    public StrategyScriptResponse generateStrategy(@RequestBody StrategyGenerationRequest request) {
        
        String userPrompt = String.format(
            "Create a strategy script for: %s. Preferred Asset: %s. Max Daily Loss: $%d", 
            request.getUserDescription(), 
            request.getPreferredAsset(), 
            request.getMaxDailyLoss()
        );

        // Uses Spring AI's structured output mapping to force Gemma to return our exact JSON DTO
        return chatClient.prompt()
                .user(userPrompt)
                .call()
                .entity(new ParameterizedTypeReference<StrategyScriptResponse>() {});
    }
}
