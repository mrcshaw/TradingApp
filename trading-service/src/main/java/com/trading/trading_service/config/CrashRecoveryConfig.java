package com.trading.trading_service.config;

import com.trading.trading_service.entity.TradeRecord;
import com.trading.trading_service.entity.TradeStatus;
import com.trading.trading_service.repository.TradeRecordRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CrashRecoveryConfig {

    private final TradeRecordRepository repository;

    public CrashRecoveryConfig(TradeRecordRepository repository) {
        this.repository = repository;
    }

    /**
     * Executes immediately when the Spring Boot application fully starts.
     * Reconciles any trades that were interrupted by a crash.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        System.out.println("--- Starting Crash Recovery Check ---");
        
        // Find trades that were sent to the broker but we crashed before getting confirmation
        List<TradeRecord> stuckTrades = repository.findByStatus(TradeStatus.SENT_TO_BROKER);
        
        if (stuckTrades.isEmpty()) {
            System.out.println("No stuck trades found. System is clean.");
            return;
        }

        System.out.println("Found " + stuckTrades.size() + " stuck trades in SENT_TO_BROKER status.");
        
        for (TradeRecord trade : stuckTrades) {
            // Phase 2: In a real scenario, we would query Tradovate via API to see if the order was filled.
            // For now, we will revert them to RECEIVED so the AsyncProcessor retries them.
            System.out.println("Reverting stuck trade " + trade.getId() + " back to RECEIVED for retry.");
            trade.setStatus(TradeStatus.RECEIVED);
            repository.save(trade);
        }
        
        System.out.println("--- Crash Recovery Complete ---");
    }
}