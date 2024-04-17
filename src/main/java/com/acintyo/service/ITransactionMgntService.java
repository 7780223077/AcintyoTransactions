package com.acintyo.service;

import java.time.LocalDateTime;
import java.util.List;

import com.acintyo.dto.HeaderDto;
import com.acintyo.dto.HistoryDto;
import com.acintyo.dto.LedgerResponse;
import com.acintyo.dto.RequestDto;
import com.acintyo.dto.TransactionDto;
import com.acintyo.dto.UpdateRequestDto;

public interface ITransactionMgntService {	
	
	LedgerResponse newTransaction(RequestDto dto);
	
	LedgerResponse updateTransaction(UpdateRequestDto dto);
	
	HeaderDto findHeader(String userId, String storeId);
	
	List<TransactionDto> findAllTransactionsofUser(String userId, String storeId, int page, int size, String sortBy, String order);
	
	List<TransactionDto> findAllTransactionsofUserBetween
		(String userId, String storeId, LocalDateTime fromDate, LocalDateTime toDate, int page, int size, String sortBy, String order);
	
	List<HistoryDto> findAllTransactionsHistoryofUser
		(String userId, String storeId, int page, int size, String sortBy, String order);
	
	List<HistoryDto> findAllTransactionsHistoryofUserBetween
		(String userId, String storeId, LocalDateTime fromDate, LocalDateTime toDate, int page, int size, String sortBy, String order);
}
