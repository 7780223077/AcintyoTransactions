package ai.acintyo.transactions.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ai.acintyo.transactions.entity.LedgerTransaction;

public interface ILedgerTransactionRepository extends JpaRepository<LedgerTransaction, Integer>{
	public Optional<LedgerTransaction> findByTransId(String transId);
	public Optional<LedgerTransaction> findByTransIdAndHeaderUserIdAndStoreId(String transId, String userId, String storeId);
	public Optional<LedgerTransaction> findByRecordIdAndTransId(int id, String transId);
	public List<LedgerTransaction> findByHeaderUserIdAndStoreIdAndTransactionDateBetween
		(String userId, String storeId, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
	public List<LedgerTransaction> findByHeaderUserIdAndStoreId(String userId, String storeId, Pageable pageable);
	
}
