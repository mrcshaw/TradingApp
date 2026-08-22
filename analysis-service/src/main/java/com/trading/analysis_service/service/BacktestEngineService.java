package com.trading.analysis_service.service;

import com.trading.analysis_service.dto.BacktestRequest;
import com.trading.analysis_service.dto.BacktestResult;
import com.trading.analysis_service.dto.HistoricalMarketDataPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class BacktestEngineService {

    private final RestTemplate restTemplate;
    private final String backtestEngineUrl;

    public BacktestEngineService(
            @Value("${backtest.engine.url:http://localhost:5000/api/backtest}") String backtestEngineUrl) {
        this.restTemplate = new RestTemplate();
        this.backtestEngineUrl = backtestEngineUrl;
    }

    public BacktestResult runBacktest(List<HistoricalMarketDataPoint> data, String pythonStrategyCode) {
        BacktestRequest request = new BacktestRequest();
        request.setData(data);
        request.setStrategy_code(pythonStrategyCode);

        // Call the Python VectorBT Microservice
        return restTemplate.postForObject(backtestEngineUrl, request, BacktestResult.class);
    }
}