package com.trading.trading_service.controller;

import com.trading.trading_service.entity.TradeRecord;
import com.trading.trading_service.service.TradeRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final TradeRecordService tradeRecordService;

    public WebhookController(TradeRecordService tradeRecordService) {
        this.tradeRecordService = tradeRecordService;
    }

    /**
     * Accepts plain text webhook payloads from TradingView.
     * Expected format is key=value pairs separated by semicolons and/or newlines.
     */
    @PostMapping(value = "/tradingview", consumes = "text/plain")
    public ResponseEntity<Map<String, String>> receiveWebhook(@RequestBody String payload) {
        Map<String, String> parsedData = parseTradingViewPayload(payload);
        
        // Example security check
        String key = parsedData.get("key");
        if (key == null || !key.equals("-b56P8DWGz89kVVdrCDGTOijrloTkDyneEEcr3K5Qbs")) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized webhook key"));
        }

        // Persistence-First: Immediately save to DB and acknowledge receipt.
        // The background async processor will pick this up for Tradovate execution.
        TradeRecord savedRecord = tradeRecordService.saveReceivedWebhook(parsedData, payload);
        
        System.out.println("Valid Webhook Received & Saved (ID: " + savedRecord.getId() + ")");
        
        return ResponseEntity.ok(parsedData);
    }

    private Map<String, String> parseTradingViewPayload(String payload) {
        Map<String, String> map = new HashMap<>();
        
        // Split by semi-colon or newline
        String[] lines = payload.split("[;\n\r]+");
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            
            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                map.put(parts[0].trim(), parts[1].trim());
            } else {
                // E.g. trailing messages without an equals sign
                map.put("alert_message", line.trim());
            }
        }
        return map;
    }
}
