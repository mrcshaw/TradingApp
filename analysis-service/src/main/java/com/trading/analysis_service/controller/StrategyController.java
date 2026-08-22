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
            .defaultSystem("You are an expert quantitative trading developer specialized in TradingView Pine Script v5. " +
                           "Convert the user's natural language trading strategy into valid Pine Script v5 code. " +
                           "The script MUST include an alert() function call when entry conditions are met. " +
                           "The alert message MUST be a strict JSON payload with the following keys: accountId, contractSymbol, action, and quantity. " +
                           "Return ONLY a valid JSON object with two fields: 'pineScript' (containing the raw code string) and 'explanation' (a brief string explaining the logic).")
            .build();
    }

    @PostMapping("/strategy/generate")
    public StrategyScriptResponse generateStrategy(@RequestBody StrategyGenerationRequest request) {
        
        String userPrompt = String.format(
            "Create a Pine Script strategy for: %s. Target Contract: %s. Default Quantity: %d contracts.", 
            request.getUserDescription(), 
            request.getContractSymbol(), 
            request.getDefaultQuantity()
        );

        String rawResponse = chatClient.prompt()
                .user(userPrompt)
                .call()
                .content();
                
        // For local small models (like gemma:2b) that struggle with pure JSON generation,
        // we wrap the raw textual response into our DTO.
        StrategyScriptResponse response = new StrategyScriptResponse();
        response.setPineScript(rawResponse);
        response.setExplanation("Generated via local Gemma model.");
        
        return response;
    }
}
