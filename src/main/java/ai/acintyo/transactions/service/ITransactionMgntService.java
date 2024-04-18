package ai.acintyo.transactions.service;

import java.time.LocalDateTime;
import java.util.List;

import ai.acintyo.transactions.dto.HeaderDto;
import ai.acintyo.transactions.dto.HistoryDto;
import ai.acintyo.transactions.dto.LedgerResponse;
import ai.acintyo.transactions.dto.RequestDto;
import ai.acintyo.transactions.dto.TransactionDto;
import ai.acintyo.transactions.dto.UpdateRequestDto;

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
