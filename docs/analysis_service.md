# Analysis Service Documentation

## Overview
The Analysis Service acts as the quantitative AI brain of the Trading Application. Instead of evaluating every live market tick (which would be latency-heavy and context-prohibitive), this service uses Google Gemma (via Ollama) to translate natural language user strategies into strict, mathematically structured JSON scripts (`StrategyScript`). These scripts are then evaluated by a fast, local Java rules engine in real-time.

## Tech Stack
- **Framework:** Spring Boot, Spring WebMVC
- **AI Integration:** Spring AI (`spring-ai-ollama-spring-boot-starter`)
- **Model Server:** Ollama (running locally on port 11434)
- **Model:** Gemma (e.g., `gemma:2b`)
- **Secrets Management:** HashiCorp Vault

## Core Workflow: Strategy Generation
1. **User Input:** The user provides a description of their trading strategy (e.g., "Buy when the 9 EMA crosses above 21 EMA"), along with parameters like `preferredAsset` and `maxDailyLoss`.
2. **Spring AI Processing:** The `StrategyController` injects these parameters into a predefined System Prompt.
3. **Structured Output:** Spring AI enforces a strict JSON schema via `ParameterizedTypeReference`. Gemma processes the prompt and returns a strictly mapped JSON object that aligns perfectly with the Java `StrategyScriptResponse` DTO.

## API Endpoints
### `POST /api/analysis/strategy/generate`
Generates a trading strategy script based on natural language input.

**Request Payload (`StrategyGenerationRequest`):**
```json
{
  "userDescription": "Buy when the 9 EMA crosses above the 21 EMA",
  "preferredAsset": "NQ",
  "maxDailyLoss": 500
}
```

**Response Payload (`StrategyScriptResponse`):**
```json
{
  "indicators": ["EMA(9)", "EMA(21)"],
  "entryConditions": ["EMA(9) > EMA(21)"],
  "riskParameters": {
    "maxDailyLoss": 500,
    "preferredAsset": "NQ"
  }
}
```

## Future Enhancements
- **Backtesting Evaluation:** Adding endpoints to feed backtest performance metrics back to Gemma for iterative strategy optimization.
- **Model Tuning:** Adjusting the `temperature` and `top_k` Spring AI parameters for more deterministic code generation.
