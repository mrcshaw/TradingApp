package com.trading.analysis_service.dto;

public class StrategyGenerationRequest {
    private String userDescription;
    private String preferredAsset;
    private int maxDailyLoss;

    public String getUserDescription() { return userDescription; }
    public void setUserDescription(String userDescription) { this.userDescription = userDescription; }
    
    public String getPreferredAsset() { return preferredAsset; }
    public void setPreferredAsset(String preferredAsset) { this.preferredAsset = preferredAsset; }
    
    public int getMaxDailyLoss() { return maxDailyLoss; }
    public void setMaxDailyLoss(int maxDailyLoss) { this.maxDailyLoss = maxDailyLoss; }
}
