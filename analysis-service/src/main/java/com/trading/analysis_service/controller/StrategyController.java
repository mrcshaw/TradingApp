package com.trading.analysis_service.controller;

import com.trading.analysis_service.dto.StrategyGenerationRequest;
import com.trading.analysis_service.dto.StrategyScriptResponse;
import com.trading.analysis_service.service.BacktestEngineService;
import com.trading.analysis_service.service.MarketDataService;
import com.trading.analysis_service.dto.HistoricalMarketDataPoint;
import com.trading.analysis_service.dto.BacktestResult;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "*") // Allow requests from our React Frontend
public class StrategyController {

    private final ChatClient chatClient;
    private final MarketDataService marketDataService;
    private final BacktestEngineService backtestEngineService;

    public StrategyController(ChatClient.Builder chatClientBuilder, MarketDataService marketDataService, BacktestEngineService backtestEngineService) {
        this.chatClient = chatClientBuilder
            .defaultSystem("You are an expert quantitative trading developer specialized in TradingView Pine Script v5 and Python VectorBT. " +
                           "Convert the user's natural language trading strategy into valid Pine Script v5 code AND valid Python code for vectorbt. " +
                           "The Python code MUST define two pandas Series variables named exactly 'entries' and 'exits' representing the boolean entry/exit signals. " +
                           "Assume the Python environment already has variables: 'close', 'open', 'high', 'low', 'volume' as pandas Series, and 'vbt' as vectorbt. " +
                           "Return ONLY a valid JSON object with three fields: 'pineScript' (containing the raw Pine Script string), 'pythonScript' (containing the Python string), and 'explanation' (a brief string explaining the logic).")
            .build();
        this.marketDataService = marketDataService;
        this.backtestEngineService = backtestEngineService;
    }

    @PostMapping("/strategy/generate")
    public StrategyScriptResponse generateStrategy(@RequestBody StrategyGenerationRequest request) {
        
        String userPrompt = String.format(
            "Create a Pine Script and Python VectorBT strategy for: %s. Target Contract: %s. Default Quantity: %d contracts.", 
            request.getUserDescription(), 
            request.getContractSymbol(), 
            request.getDefaultQuantity()
        );

        String rawResponse = chatClient.prompt()
                .user(userPrompt)
                .call()
                .content();
                
        StrategyScriptResponse response = new StrategyScriptResponse();
        // Since we are not strictly parsing the JSON locally in this fallback, we just dump it for debugging.
        response.setPineScript(rawResponse);
        response.setExplanation("Generated via local Gemma model.");
        
        return response;
    }
    
    @PostMapping("/strategy/test")
    public BacktestResult testStrategy(@RequestBody Map<String, String> request) {
        String symbol = request.getOrDefault("symbol", "ES=F");
        String pythonLogic = request.get("pythonScript");
        
        if (pythonLogic == null || pythonLogic.isEmpty()) {
            throw new IllegalArgumentException("Python script is required for backtesting");
        }

        // 1. Fetch real historical data
        List<HistoricalMarketDataPoint> historicalData = marketDataService.getRecentOneMinuteData(symbol);
        
        // 2. Execute against Python VectorBT Microservice
        BacktestResult result = backtestEngineService.runBacktest(historicalData, pythonLogic);
        result.setSymbol(symbol);
        
        return result;
    }

    @GetMapping("/marketdata/test")
    public List<HistoricalMarketDataPoint> testMarketData(@RequestParam(defaultValue = "ES=F") String symbol) {
        return marketDataService.getRecentOneMinuteData(symbol);
    }
}
