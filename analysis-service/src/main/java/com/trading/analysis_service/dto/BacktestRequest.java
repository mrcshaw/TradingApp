package com.trading.analysis_service.dto;

import java.util.List;

public class BacktestRequest {
    private List<HistoricalMarketDataPoint> data;
    private String strategy_code; // Note: Python API expects snake_case for this field

    public List<HistoricalMarketDataPoint> getData() { return data; }
    public void setData(List<HistoricalMarketDataPoint> data) { this.data = data; }

    public String getStrategy_code() { return strategy_code; }
    public void setStrategy_code(String strategy_code) { this.strategy_code = strategy_code; }
}