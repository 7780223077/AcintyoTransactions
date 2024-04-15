package com.acintyo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.acintyo.entity.LedgerTransaction;

public interface ILedgerTransactionRepository extends JpaRepository<LedgerTransaction, Integer>{
	public Optional<LedgerTransaction> findByTransId(String transId);
	public Optional<LedgerTransaction> findByTransIdAndUserIdAndStoreId(String transId, String userId, String storeId);
	public Optional<LedgerTransaction> findByRecordIdAndTransId(int id, String transId);
	public List<LedgerTransaction> findByUserIdAndStoreIdAndTransactionDateBetweenOrderByTransactionDateDesc
		(String userId, String storeId, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
	public List<LedgerTransaction> findByUserIdAndStoreIdOrderByTransactionDateDesc(String userId, String storeId, Pageable pageable);
}
