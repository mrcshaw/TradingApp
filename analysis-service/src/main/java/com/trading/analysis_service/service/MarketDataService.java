package com.trading.analysis_service.service;

import com.trading.analysis_service.dto.HistoricalMarketDataPoint;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MarketDataService {

    private final RestTemplate restTemplate;

    public MarketDataService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Fetches 1-minute historical data from Yahoo Finance for a given futures symbol.
     * Note: Yahoo Finance provides continuous futures data.
     * ES = "ES=F"
     * NQ = "NQ=F"
     * 
     * @param symbol The Yahoo Finance ticker (e.g. "ES=F")
     * @return A list of 1-minute OHLCV candles
     */
    public List<HistoricalMarketDataPoint> getRecentOneMinuteData(String symbol) {
        String url = String.format("https://query1.finance.yahoo.com/v8/finance/chart/%s?range=7d&interval=1m", symbol);
        List<HistoricalMarketDataPoint> dataPoints = new ArrayList<>();
        
        try {
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);
            
            org.springframework.http.ResponseEntity<Map> responseEntity = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, Map.class);
            Map<String, Object> response = responseEntity.getBody();
            
            if (response == null || !response.containsKey("chart")) {
                throw new RuntimeException("Invalid response from Yahoo Finance");
            }
            
            Map<String, Object> chart = (Map<String, Object>) response.get("chart");
            List<Map<String, Object>> result = (List<Map<String, Object>>) chart.get("result");
            
            if (result == null || result.isEmpty() || result.get(0) == null) {
                return dataPoints; // Empty list
            }
            
            Map<String, Object> data = result.get(0);
            List<Integer> timestamps = (List<Integer>) data.get("timestamp");
            
            if (timestamps == null || timestamps.isEmpty()) {
                return dataPoints;
            }
            
            Map<String, Object> indicators = (Map<String, Object>) data.get("indicators");
            List<Map<String, Object>> quoteList = (List<Map<String, Object>>) indicators.get("quote");
            Map<String, Object> quotes = quoteList.get(0);
            
            List<Object> opens = (List<Object>) quotes.get("open");
            List<Object> highs = (List<Object>) quotes.get("high");
            List<Object> lows = (List<Object>) quotes.get("low");
            List<Object> closes = (List<Object>) quotes.get("close");
            List<Object> volumes = (List<Object>) quotes.get("volume");
            
            for (int i = 0; i < timestamps.size(); i++) {
                // Yahoo sometimes returns nulls for sparse minutes
                if (opens == null || closes == null || opens.size() <= i || closes.size() <= i) continue;
                if (opens.get(i) == null || closes.get(i) == null) continue;
                
                HistoricalMarketDataPoint dp = new HistoricalMarketDataPoint();
                dp.setSymbol(symbol);
                dp.setTimestamp(LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamps.get(i)), ZoneId.of("America/New_York")));
                
                dp.setOpen(opens.get(i) != null ? ((Number)opens.get(i)).doubleValue() : 0.0);
                dp.setHigh(highs.get(i) != null ? ((Number)highs.get(i)).doubleValue() : 0.0);
                dp.setLow(lows.get(i) != null ? ((Number)lows.get(i)).doubleValue() : 0.0);
                dp.setClose(closes.get(i) != null ? ((Number)closes.get(i)).doubleValue() : 0.0);
                dp.setVolume(volumes != null && volumes.size() > i && volumes.get(i) != null ? ((Number)volumes.get(i)).longValue() : 0L);
                
                dataPoints.add(dp);
            }
            
        } catch (Exception e) {
            System.err.println("Error fetching data from Yahoo Finance: " + e.getMessage());
            e.printStackTrace();
        }
        
        return dataPoints;
    }
}