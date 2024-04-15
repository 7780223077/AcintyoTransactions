package com.acintyo.service;

import java.time.LocalDateTime;
import java.util.List;

import com.acintyo.dto.LedgerResponse;
import com.acintyo.entity.LedgerHeader;
import com.acintyo.entity.LedgerTransaction;
import com.acintyo.entity.LedgerTransactionHistory;

public interface ITransactionMgntService {	
	LedgerResponse doTransaction(LedgerTransaction ct, boolean newTrans);
	
	LedgerHeader findHeader(String userId, String storeId);
	
	List<LedgerTransaction> findAllTransactionsofUser(String userId, String storeId, int page, int size);
	
	List<LedgerTransaction> findAllTransactionsofUserBetween
		(String userId, String storeId, LocalDateTime fromDate, LocalDateTime toDate, int page, int size);
	
	List<LedgerTransactionHistory> findAllTransactionsHistoryofUser
		(String userId, String storeId, int page, int size);
	
	List<LedgerTransactionHistory> findAllTransactionsHistoryofUserBetween
		(String userId, String storeId, LocalDateTime fromDate, LocalDateTime toDate, int page, int size);
}
