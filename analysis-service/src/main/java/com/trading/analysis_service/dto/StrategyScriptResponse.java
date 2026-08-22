package com.trading.analysis_service.dto;

import java.util.List;

public class StrategyScriptResponse {
    private List<String> indicators;
    private List<String> entryConditions;
    private RiskParameters riskParameters;

    // Default constructor for Jackson
    public StrategyScriptResponse() {}

    public List<String> getIndicators() { return indicators; }
    public void setIndicators(List<String> indicators) { this.indicators = indicators; }

    public List<String> getEntryConditions() { return entryConditions; }
    public void setEntryConditions(List<String> entryConditions) { this.entryConditions = entryConditions; }

    public RiskParameters getRiskParameters() { return riskParameters; }
    public void setRiskParameters(RiskParameters riskParameters) { this.riskParameters = riskParameters; }

    public static class RiskParameters {
        private int maxDailyLoss;
        private String preferredAsset;

        public RiskParameters() {}

        public int getMaxDailyLoss() { return maxDailyLoss; }
        public void setMaxDailyLoss(int maxDailyLoss) { this.maxDailyLoss = maxDailyLoss; }

        public String getPreferredAsset() { return preferredAsset; }
        public void setPreferredAsset(String preferredAsset) { this.preferredAsset = preferredAsset; }
    }
}
