package com.trading.trading_service.service;

import com.trading.trading_service.entity.TradeRecord;
import com.trading.trading_service.entity.TradeStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TradeAsyncProcessor {

    private final TradeRecordService recordService;
    private final TradovateExecutionService executionService;

    public TradeAsyncProcessor(TradeRecordService recordService, TradovateExecutionService executionService) {
        this.recordService = recordService;
        this.executionService = executionService;
    }

    /**
     * Polls the database for trades stuck in RECEIVED status.
     * This acts as the Outbox pattern processor.
     */
    @Scheduled(fixedDelay = 2000) // Runs every 2 seconds
    public void processPendingTrades() {
        List<TradeRecord> pendingTrades = recordService.getPendingTrades();
        
        for (TradeRecord trade : pendingTrades) {
            try {
                // 1. Mark as executing so other threads don't pick it up (in a distributed system, we'd use pessimistic locking)
                trade = recordService.updateStatus(trade, TradeStatus.SENT_TO_BROKER);
                
                // 2. Call Broker API
                executionService.executeTrade(trade);
                
                // 3. Mark as completed
                // In reality, we'd save the broker's Order ID here
                recordService.updateStatus(trade, TradeStatus.FILLED);
                
                System.out.println("Successfully processed TradeRecord: " + trade.getId());
                
            } catch (Exception e) {
                // If it fails, mark as failed so it doesn't infinitely loop (or implement a retry policy)
                System.err.println("Failed to process trade " + trade.getId() + ": " + e.getMessage());
                recordService.updateStatus(trade, TradeStatus.FAILED);
            }
        }
    }
}