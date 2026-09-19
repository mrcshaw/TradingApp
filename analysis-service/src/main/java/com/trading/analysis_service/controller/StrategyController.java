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
                           "Convert the user natural language trading strategy into valid Pine Script v5 code AND valid Python code for vectorbt. " +
                           "CRITICAL PINE SCRIPT v5 RULES: " +
                           "1. MUST start with //@version=5 " +
                           "2. MUST use strategy('Name', overlay=true) not indicator(). " +
                           "3. MUST use the ta. prefix for all technical analysis functions. " +
                           "4. MUST use the math. prefix for math functions. " +
                           "5. Variable reassignment MUST use := not =. " +
                           "6. EXACT SYNTAX for entry/exit: strategy.entry('LongID', strategy.long, when=condition) and strategy.close('LongID', when=condition). " +
                           "7. Do not use undeclared variables. " +
                           "CRITICAL PYTHON RULES: " +
                           "1. Strictly define two boolean pandas Series variables named exactly entries and exits. " +
                           "2. Assume close, open, high, low, volume (pandas Series) and vbt (vectorbt) are already in the global namespace. " +
                           "Format your response strictly with two markdown code blocks: one labeled ```pine for the Pine Script, and one labeled ```python for the Python code.")
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
        
        // Use robust Regex extraction to pull out code blocks regardless of how the LLM formatted the response.
        java.util.regex.Pattern pinePattern = java.util.regex.Pattern.compile("```(?:pine|pinescript)(.*?)```", java.util.regex.Pattern.DOTALL | java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher pineMatcher = pinePattern.matcher(rawResponse);
        if (pineMatcher.find()) {
            response.setPineScript(pineMatcher.group(1).trim());
        } else {
            response.setPineScript("// Failed to extract Pine Script from response.\n" + rawResponse);
        }

        java.util.regex.Pattern pythonPattern = java.util.regex.Pattern.compile("```python(.*?)```", java.util.regex.Pattern.DOTALL | java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher pythonMatcher = pythonPattern.matcher(rawResponse);
        if (pythonMatcher.find()) {
            response.setPythonScript(pythonMatcher.group(1).trim());
        } else {
            response.setPythonScript("# Failed to extract Python code.");
        }

        // Try to parse out the explanation or JSON if it exists, otherwise just dump it.
        try {
            java.util.regex.Pattern jsonPattern = java.util.regex.Pattern.compile("```json(.*?)```", java.util.regex.Pattern.DOTALL | java.util.regex.Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher jsonMatcher = jsonPattern.matcher(rawResponse);
            if (jsonMatcher.find()) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
                mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
                StrategyScriptResponse jsonResponse = mapper.readValue(jsonMatcher.group(1).trim(), StrategyScriptResponse.class);
                if (jsonResponse.getExplanation() != null) response.setExplanation(jsonResponse.getExplanation());
                if (pineMatcher.find() == false && jsonResponse.getPineScript() != null) response.setPineScript(jsonResponse.getPineScript());
                if (pythonMatcher.find() == false && jsonResponse.getPythonScript() != null) response.setPythonScript(jsonResponse.getPythonScript());
            } else {
                 response.setExplanation("See generated code blocks.");
            }
        } catch (Exception e) {
             response.setExplanation("Failed to parse detailed explanation.");
        }

        if (response.getExplanation() == null || response.getExplanation().isEmpty()) {
            response.setExplanation("Strategy generated from user prompt.");
        }
        
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
