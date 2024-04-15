package com.acintyo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acintyo.entity.LedgerHeader;

public interface ILedgerHeaderRepository extends JpaRepository<LedgerHeader, String>{
	public Optional<LedgerHeader> findByUserIdAndStoreId(String userId, String storeId);
	
}
