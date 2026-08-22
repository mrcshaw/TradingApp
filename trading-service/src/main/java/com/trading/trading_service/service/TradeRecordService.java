package com.trading.trading_service.service;

import com.trading.trading_service.entity.TradeRecord;
import com.trading.trading_service.entity.TradeStatus;
import com.trading.trading_service.repository.TradeRecordRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.List;

@Service
public class TradeRecordService {

    private final TradeRecordRepository repository;

    public TradeRecordService(TradeRecordRepository repository) {
        this.repository = repository;
    }

    public TradeRecord saveReceivedWebhook(Map<String, String> parsedData, String rawPayload) {
        TradeRecord record = new TradeRecord();
        record.setAccountId(parsedData.get("account"));
        record.setInstrument(parsedData.get("instrument"));
        record.setAction(parsedData.get("action"));
        
        try {
            if (parsedData.containsKey("qty")) {
                record.setQuantity(Integer.parseInt(parsedData.get("qty")));
            }
        } catch (NumberFormatException e) {
            // log error
            record.setQuantity(0);
        }
        
        record.setStatus(TradeStatus.RECEIVED);
        record.setRawPayload(rawPayload);
        
        return repository.save(record);
    }
    
    public List<TradeRecord> getPendingTrades() {
        return repository.findByStatus(TradeStatus.RECEIVED);
    }
    
    public TradeRecord updateStatus(TradeRecord record, TradeStatus newStatus) {
        record.setStatus(newStatus);
        return repository.save(record);
    }
}