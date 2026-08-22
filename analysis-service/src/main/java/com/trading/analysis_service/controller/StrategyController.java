package com.trading.analysis_service.controller;

import com.trading.analysis_service.dto.StrategyGenerationRequest;
import com.trading.analysis_service.dto.StrategyScriptResponse;
import com.trading.analysis_service.service.MarketDataService;
import com.trading.analysis_service.dto.HistoricalMarketDataPoint;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
public class StrategyController {

    private final ChatClient chatClient;
    private final MarketDataService marketDataService;

    public StrategyController(ChatClient.Builder chatClientBuilder, MarketDataService marketDataService) {
        this.chatClient = chatClientBuilder
            .defaultSystem("You are an expert quantitative trading developer specialized in TradingView Pine Script v5. " +
                           "Convert the user's natural language trading strategy into valid Pine Script v5 code. " +
                           "Return ONLY a valid JSON object with two fields: 'pineScript' (containing the raw code string) and 'explanation' (a brief string explaining the logic).")
            .build();
        this.marketDataService = marketDataService;
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
    
    @GetMapping("/marketdata/test")
    public List<HistoricalMarketDataPoint> testMarketData(@RequestParam(defaultValue = "ES=F") String symbol) {
        return marketDataService.getRecentOneMinuteData(symbol);
    }
}
