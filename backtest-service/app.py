from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Optional
import pandas as pd

# Workaround for Plotly/VectorBT theme bug in v0.26.0
import plotly.io as pio
pio.templates.default = "plotly_dark"

import vectorbt as vbt

app = FastAPI(title="TradingApp Backtest Engine")

class DataPoint(BaseModel):
    symbol: str
    timestamp: str
    open: float
    high: float
    low: float
    close: float
    volume: int

class BacktestRequest(BaseModel):
    data: List[DataPoint]
    strategy_code: str

@app.post("/api/backtest")
def run_backtest(request: BacktestRequest):
    if not request.data:
        raise HTTPException(status_code=400, detail="No historical data provided")

    # 1. Convert Data to Pandas DataFrame
    df = pd.DataFrame([dp.dict() for dp in request.data])
    df['timestamp'] = pd.to_datetime(df['timestamp'])
    df.set_index('timestamp', inplace=True)
    
    # Expose OHLCV for the dynamic script
    open_price = df['open']
    high = df['high']
    low = df['low']
    close = df['close']
    volume = df['volume']

    # 2. Execute Dynamic Strategy Code
    local_vars = {
        'open': open_price,
        'high': high,
        'low': low,
        'close': close,
        'volume': volume,
        'pd': pd,
        'vbt': vbt
    }
    
    try:
        exec(request.strategy_code, {}, local_vars)
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"Strategy execution failed: {str(e)}")

    if 'entries' not in local_vars or 'exits' not in local_vars:
         raise HTTPException(status_code=400, detail="Strategy code must define 'entries' and 'exits' variables")

    entries = local_vars['entries']
    exits = local_vars['exits']

    # 3. Run VectorBT Portfolio Simulation
    try:
        # Override the default plotting template immediately before simulation
        vbt.settings.set_theme("dark") # Safe theme that doesn't trigger Plotly HeatmapGL issues in v0.26
        
        portfolio = vbt.Portfolio.from_signals(
            close,
            entries=entries,
            exits=exits,
            init_cash=100000,
            fees=0.0001 # Small simulated fee
        )

        return {
            "netProfit": portfolio.total_profit() if not pd.isna(portfolio.total_profit()) else 0.0,
            "maxDrawdown": portfolio.max_drawdown() if not pd.isna(portfolio.max_drawdown()) else 0.0,
            "totalTrades": int(portfolio.trades.count()),
            "winRate": portfolio.trades.win_rate() if not pd.isna(portfolio.trades.win_rate()) else 0.0,
            "stats": portfolio.stats().to_dict() # Full stats for deep dive
        }
    except Exception as e:
         raise HTTPException(status_code=500, detail=f"Backtest simulation failed: {str(e)}")

@app.get("/api/health")
def health_check():
    return {"status": "Backtest Engine is running"}
