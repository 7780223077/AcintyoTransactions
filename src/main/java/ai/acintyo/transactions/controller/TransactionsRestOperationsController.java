package ai.acintyo.transactions.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.acintyo.transactions.dto.HeaderDto;
import ai.acintyo.transactions.dto.HistoryDto;
import ai.acintyo.transactions.dto.LedgerResponse;
import ai.acintyo.transactions.dto.RequestDto;
import ai.acintyo.transactions.dto.TransactionDto;
import ai.acintyo.transactions.dto.UpdateRequestDto;
import ai.acintyo.transactions.service.ITransactionMgntService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/transactions")
@Slf4j
public class TransactionsRestOperationsController {
	@Autowired
	private ITransactionMgntService service;
	 
	@PostMapping("/new-transaction")
	public ResponseEntity<LedgerResponse> newTransaction(@Valid @RequestBody RequestDto request) {
		log.debug("New Transaction started");
		LedgerResponse response = service.newTransaction(request);
		return new ResponseEntity<LedgerResponse>(response,HttpStatus.OK);
	}
	
	@PutMapping("/update-transaction")
	public ResponseEntity<LedgerResponse> updateTransaction(@Valid @RequestBody UpdateRequestDto request)  {
		LedgerResponse response = service.updateTransaction(request);
		return new ResponseEntity<LedgerResponse>(response,HttpStatus.OK);
	} 
	
	@GetMapping("/get-header-details/{userId}/{storeId}")
	public ResponseEntity<HeaderDto> getHeader(
			@PathVariable String userId,
			@PathVariable String storeId) {
		HeaderDto header = service.findHeader(userId.toUpperCase(), storeId.toUpperCase());
		return new ResponseEntity<HeaderDto>(header,HttpStatus.CREATED);
	}
	
	@GetMapping("/get-ledger-transactions/{userId}/{storeId}")
	public ResponseEntity<List<TransactionDto>> getTransactions(
			@PathVariable String userId, 
			@PathVariable String storeId, 
			@RequestParam(required = false) int page,
			@RequestParam(required = false, defaultValue = "20") int size,
			@RequestParam(required = false, defaultValue = "transactionDate") String sortBy,
			@RequestParam(required = false, defaultValue = "DESC") String order) {
		List<TransactionDto> allTransactionsofUser = service
				.findAllTransactionsofUser(userId.toUpperCase(), storeId.toUpperCase(), page, size, sortBy, order);
		return new ResponseEntity<List<TransactionDto>>(allTransactionsofUser,HttpStatus.CREATED);
	}
	
	@GetMapping("/get-ledger-transactions-between/{userId}/{storeId}")
	public ResponseEntity<List<TransactionDto>> getTransactions(
			@PathVariable String userId, 
			@PathVariable String storeId,
			@RequestParam LocalDateTime fromDate,
			@RequestParam LocalDateTime toDate, 
			@RequestParam(required = false) int page,
			@RequestParam(required = false, defaultValue = "20") int size,
			@RequestParam(required = false, defaultValue = "transactionDate") String sortBy,
			@RequestParam(required = false, defaultValue = "DESC") String order) {
		List<TransactionDto> allTransactionsofUser = service
				.findAllTransactionsofUserBetween(userId.toUpperCase(), storeId.toUpperCase(),fromDate,toDate, page, size, sortBy, order);
		return new ResponseEntity<List<TransactionDto>>(allTransactionsofUser,HttpStatus.CREATED);
	}
	
	@GetMapping("/get-ledger-history/{userId}/{storeId}")
	public ResponseEntity<List<HistoryDto>> getTransactionsHistory(
			@PathVariable String userId, 
			@PathVariable String storeId, 
			@RequestParam(required = false) int page,
			@RequestParam(required = false,defaultValue = "20") int size,
			@RequestParam(required = false, defaultValue = "transactionDate") String sortBy,
			@RequestParam(required = false, defaultValue = "DESC") String order){
		List<HistoryDto> allTransactionsofUser = service
				.findAllTransactionsHistoryofUser(userId.toUpperCase(), storeId.toUpperCase(), page, size, sortBy, order);
		return new ResponseEntity<List<HistoryDto>>(allTransactionsofUser,HttpStatus.CREATED);
	}
	
	@GetMapping("/get-ledger-history-between/{userId}/{storeId}")
	public ResponseEntity<List<HistoryDto>> getTransactionsHistory(
			@PathVariable String userId, 
			@PathVariable String storeId,
			@RequestParam LocalDateTime fromDate, 
			@RequestParam LocalDateTime toDate,
			@RequestParam(required = false) int page,
			@RequestParam(required = false, defaultValue = "20") int size,
			@RequestParam(required = false, defaultValue = "transactionDate") String sortBy,
			@RequestParam(required = false, defaultValue = "DESC") String order) {
		List<HistoryDto> allTransactionsofUser = service
				.findAllTransactionsHistoryofUserBetween(userId.toUpperCase(), storeId.toUpperCase(),fromDate,toDate, page, size, sortBy, order);
		return new ResponseEntity<List<HistoryDto>>(allTransactionsofUser,HttpStatus.CREATED);
	}
	
	
}
