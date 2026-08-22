package com.trading.analysis_service.dto;

public class StrategyScriptResponse {
    private String pineScript;
    private String explanation;

    // Default constructor for Jackson
    public StrategyScriptResponse() {}

    public String getPineScript() { return pineScript; }
    public void setPineScript(String pineScript) { this.pineScript = pineScript; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
