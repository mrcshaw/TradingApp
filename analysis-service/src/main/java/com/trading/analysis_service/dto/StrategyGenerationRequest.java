package com.trading.analysis_service.dto;

public class StrategyGenerationRequest {
    private String userDescription;
    private String contractSymbol; // e.g. "ES1!", "NQ1!", "MES"
    private int maxDailyLoss;
    private int defaultQuantity;

    public String getUserDescription() { return userDescription; }
    public void setUserDescription(String userDescription) { this.userDescription = userDescription; }
    
    public String getContractSymbol() { return contractSymbol; }
    public void setContractSymbol(String contractSymbol) { this.contractSymbol = contractSymbol; }
    
    public int getMaxDailyLoss() { return maxDailyLoss; }
    public void setMaxDailyLoss(int maxDailyLoss) { this.maxDailyLoss = maxDailyLoss; }

    public int getDefaultQuantity() { return defaultQuantity; }
    public void setDefaultQuantity(int defaultQuantity) { this.defaultQuantity = defaultQuantity; }
}
