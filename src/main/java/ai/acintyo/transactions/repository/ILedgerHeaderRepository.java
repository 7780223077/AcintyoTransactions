package ai.acintyo.transactions.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ai.acintyo.transactions.entity.LedgerHeader;

public interface ILedgerHeaderRepository extends JpaRepository<LedgerHeader, String>{
	public Optional<LedgerHeader> findByUserIdIgnoreCaseAndStoreIdIgnoreCase(String userId, String storeId);
}
