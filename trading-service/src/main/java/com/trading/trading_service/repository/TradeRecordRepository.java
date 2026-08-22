package com.trading.trading_service.repository;

import com.trading.trading_service.entity.TradeRecord;
import com.trading.trading_service.entity.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRecordRepository extends JpaRepository<TradeRecord, String> {
    List<TradeRecord> findByStatus(TradeStatus status);
}