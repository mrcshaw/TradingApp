package com.trading.trading_service.service;

import com.trading.trading_service.entity.TradeRecord;
import org.springframework.stereotype.Service;

@Service
public class TradovateExecutionService {

    public void executeTrade(TradeRecord record) {
        // Phase 2: This is where we will map the TradeRecord to the Tradovate /order/placeorder JSON
        // and make the REST call using WebClient or RestTemplate.
        
        System.out.println(">>> (MOCK) Executing Trade in Tradovate for Record ID: " + record.getId());
        System.out.println("    Action: " + record.getAction() + " " + record.getQuantity() + " " + record.getInstrument());
        
        // Simulating successful network call
        try {
            Thread.sleep(500); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}