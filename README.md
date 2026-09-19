# Gemma Chatbot Application & Backtest Engine

This repository contains a Spring Boot application acting as an interface for a local Gemma LLM model to function as an AI Quantitative Developer Chatbot. The chatbot has direct access to a Python VectorBT Backtesting Engine to run and verify the trade ideas it generates using historical market data.

## 🚀 Prerequisites
Ensure you have the following installed on your host machine:
*   **Java 17** (or portable JDK mapped to `JAVA_HOME`)
*   **Docker Desktop** (Required to run the Python Backtest Engine)
*   **Ollama** (Installed natively on Windows/Mac for performance)

---

## 🛠️ Step 1: Start the Backtest Engine

The Python backtesting engine handles the heavy lifting of simulating the strategies. Start it using Docker Compose:

```powershell
cd C:\Development\TradingApp
docker compose up -d backtest-engine
```
*(Note: The backtest engine runs on port 5000).*

---

## 🛠️ Step 2: Start Local LLM (Ollama)
Ensure Ollama is running natively. Download the `gemma:7b` model:
```powershell
ollama pull gemma:7b
ollama run gemma:7b
```
*(Type `/bye` to exit the terminal chat, the model remains available via REST API on `localhost:11434`).*

---

## 🏃 Step 3: Start the Backend Chatbot Service

In a new terminal window, start the Spring Boot service:

```powershell
cd C:\Development\TradingApp\analysis-service
.\mvnw spring-boot:run
```
*(Note: The service runs on Port 8082).*

---

## 💾 Committing & Pushing to GitHub

To save your work and push updates to the backend repository:

```powershell
cd C:\Development\TradingApp

# View changed files
git status

# Stage all changes
git add .

# Commit changes with a descriptive message
git commit -m "refactor: cleaned up project, simplified architecture to Chatbot and Backtest engine only"

# Push to the current branch
git push origin feature/analysis-engine
```

*To run the User Interface, please follow the instructions in the `TradingApp-Client` repository.*