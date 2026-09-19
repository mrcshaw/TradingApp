# Trading Application (Backend & Analysis Engine)

This repository contains the backend microservices and Python backtesting engine for the quantitative Trading Application. It relies on Spring Boot, Python FastAPI, VectorBT, PostgreSQL, Redis, HashiCorp Vault, and native Ollama for LLM generation.

## 🚀 Prerequisites
Ensure you have the following installed on your host machine:
*   **Java 17** (or portable JDK mapped to `JAVA_HOME`)
*   **Python 3.11+**
*   **Docker Desktop** (for PostgreSQL & Redis)
*   **HashiCorp Vault**
*   **Ollama** (Installed natively on Windows/Mac, not in Docker, for performance)

## 🛠️ Step 1: Start Infrastructure & Secrets

### 1. Database & Cache
Start the PostgreSQL database and Redis cache using Docker Compose:
```powershell
cd C:\Development\TradingApp
docker compose up -d postgres redis
```
*(Note: PostgreSQL runs on port 5432, Redis on 6379).*

### 2. HashiCorp Vault (Secrets Management)
Start a local Vault dev server in a new terminal window:
```powershell
vault server -dev -dev-root-token-id="root" -dev-listen-address="127.0.0.1:8200"
```
Keep this terminal running. In another terminal, seed the database credentials:
```powershell
$env:VAULT_ADDR="http://127.0.0.1:8200"
$env:VAULT_TOKEN="root"
vault kv put secret/application spring.datasource.username="trading_user" spring.datasource.password="trading_password"
```

### 3. Local LLM (Ollama)
Ensure Ollama is running natively. Download the `gemma:7b` (or `qwen2.5-coder:7b`) model:
```powershell
ollama pull gemma:7b
ollama run gemma:7b
```
*(Type `/bye` to exit the chat, the model remains available via REST API on `localhost:11434`).*

---

## 🏃 Step 2: Start the Application Services

### 1. Python Backtest Engine
Open a new terminal, install dependencies, and start the FastAPI backtesting service:
```powershell
cd C:\Development\TradingApp\backtest-service
pip install -r requirements.txt
uvicorn app:app --host 0.0.0.1 --port 5000 --reload
```

### 2. Java Microservices
In separate terminal windows, start the required Spring Boot services:

**API Gateway (Port 8080):**
```powershell
cd C:\Development\TradingApp\api-gateway
.\mvnw spring-boot:run
```

**Account Service (Port 8081):**
```powershell
cd C:\Development\TradingApp\account-service
.\mvnw spring-boot:run
```

**Analysis Service (Port 8082):**
```powershell
cd C:\Development\TradingApp\analysis-service
.\mvnw spring-boot:run
```

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
git commit -m "feat: added new backend feature"

# Push to the current branch (e.g., development or feature branch)
git push origin <branch-name>
```

---

## 📖 Additional Documentation
*   [Architecture & System Design](./system_design.adoc)
*   [Infrastructure Details](./docs/infrastructure_setup.md)

*To run the User Interface, please follow the instructions in the `TradingApp-Client` repository.*