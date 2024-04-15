package com.acintyo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.acintyo.entity.LedgerTransactionHistory;

public interface ILedgerTransactionHistoryRepository extends JpaRepository<LedgerTransactionHistory, Integer>{
	List<LedgerTransactionHistory> findByUserIdAndStoreIdAndTransactionDateBetween
				(String userid, String storeId, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
	List<LedgerTransactionHistory> findByUserIdAndStoreId(String userId, String stoteId, Pageable pageable);
}
