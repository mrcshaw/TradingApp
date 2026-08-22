package com.trading.trading_service.entity;

public enum TradeStatus {
    RECEIVED,          // Webhook received and persisted
    SENT_TO_BROKER,    // Sent to Tradovate but waiting for confirmation
    FILLED,            // Tradovate confirmed filled
    FAILED             // Failed validation or rejected by broker
}