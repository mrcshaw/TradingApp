# Analysis Service Documentation

## Overview
The Analysis Service acts as the quantitative AI developer of the Trading Application. Instead of evaluating every live market tick locally, this service uses Google Gemma (via Ollama) to translate natural language user strategies into fully functional **TradingView Pine Script v5** code. 

Users deploy this Pine Script directly in TradingView. TradingView handles the live charting and tick evaluation, and fires Webhook alerts back to our `Trading Service` to execute trades on Tradovate.

## Tech Stack
- **Framework:** Spring Boot, Spring WebMVC
- **AI Integration:** Spring AI (`spring-ai-ollama-spring-boot-starter`)
- **Model Server:** Ollama (running locally on port 11434)
- **Model:** Gemma (e.g., `gemma:2b`)
- **Secrets Management:** HashiCorp Vault

## Core Workflow: Pine Script Generation
1. **User Input:** The user provides a description of their trading strategy (e.g., "Buy when the 9 EMA crosses above 21 EMA"), along with preferences.
2. **Spring AI Processing:** The `StrategyController` instructs Gemma to generate valid Pine Script v5 code designed to trigger `alert()` function calls containing our exact Webhook JSON payload.
3. **Structured Output:** Spring AI enforces a JSON response containing the raw `pineScript` string and a plain text `explanation`.

## API Endpoints
### `POST /api/analysis/strategy/generate`
Generates a Pine Script v5 strategy based on natural language input.

**Request Payload (`StrategyGenerationRequest`):**
```json
{
  "userDescription": "Buy when the 9 EMA crosses above the 21 EMA",
  "contractSymbol": "ES1!",
  "maxDailyLoss": 500,
  "defaultQuantity": 2
}
```

**Response Payload (`StrategyScriptResponse`):**
```json
{
  "pineScript": "//@version=5\nstrategy(\"My EMA Strategy\", overlay=true)\n...",
  "explanation": "This script utilizes a 9-period EMA and a 21-period EMA..."
}
```

## Future Enhancements
- **Backtesting Engine:** Adding an internal Java-based backtesting engine to simulate strategies against historical CSV or Tradovate data before deploying them to TradingView.
- **Iterative Refinement:** Allowing the user to send backtest results back to Gemma to mathematically refine the Pine Script parameters.
