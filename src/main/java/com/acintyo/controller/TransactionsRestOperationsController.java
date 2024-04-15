package com.acintyo.controller;

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

import com.acintyo.dto.LedgerResponse;
import com.acintyo.dto.RequestDto;
import com.acintyo.dto.UpdateRequestDto;
import com.acintyo.entity.LedgerHeader;
import com.acintyo.entity.LedgerTransaction;
import com.acintyo.entity.LedgerTransactionHistory;
import com.acintyo.service.ITransactionMgntService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/transactions")
public class TransactionsRestOperationsController {
	@Autowired
	private ITransactionMgntService service;

	@GetMapping("/display")
	public ResponseEntity<String> displayMsg() {
		return new ResponseEntity<String>("Hello world", HttpStatus.OK);
	}
	 
	@PostMapping("/new-transaction")
	public ResponseEntity<LedgerResponse> newTransaction(@Valid @RequestBody RequestDto request) {
		LedgerTransaction transaction = new LedgerTransaction(request);
		LedgerResponse response = service.doTransaction(transaction, true);
		return new ResponseEntity<LedgerResponse>(response,HttpStatus.OK);
	}
	
	@PutMapping("/update-transaction")
	public ResponseEntity<LedgerResponse> updateTransaction(@Valid @RequestBody UpdateRequestDto request)  {
		LedgerTransaction transaction = new LedgerTransaction(request);
		LedgerResponse response = service.doTransaction(transaction, false);
		return new ResponseEntity<LedgerResponse>(response,HttpStatus.OK);
	} 
	
	@GetMapping("/get-header-details/{userId}/{storeId}")
	public ResponseEntity<LedgerHeader> getHeader(
			@PathVariable String userId,
			@PathVariable String storeId) {
		LedgerHeader header = service.findHeader(userId, storeId);
		return new ResponseEntity<LedgerHeader>(header,HttpStatus.CREATED);
	}
	
	@GetMapping("/get-ledger-transactions/{userId}/{storeId}")
	public ResponseEntity<List<LedgerTransaction>> getTransactions(
				@PathVariable String userId, 
				@PathVariable String storeId, 
				@RequestParam(required = false) int page,
				@RequestParam(required = false) int size,
				@RequestParam String sortBy,
				@RequestParam String order) {
		List<LedgerTransaction> allTransactionsofUser = service
				.findAllTransactionsofUser(userId, storeId, page, size, sortBy, order);
		return new ResponseEntity<List<LedgerTransaction>>(allTransactionsofUser,HttpStatus.CREATED);
	}
	
	@GetMapping("/get-ledger-transactions-between/{userId}/{storeId}")
	public ResponseEntity<List<LedgerTransaction>> getTransactions(
			@PathVariable String userId, 
			@PathVariable String storeId,
			@RequestParam LocalDateTime fromDate,
			@RequestParam LocalDateTime toDate, 
			@RequestParam(required = false) int page,
			@RequestParam(required = false) int size,
			@RequestParam String sortBy,
			@RequestParam String order) {
		List<LedgerTransaction> allTransactionsofUser = service
				.findAllTransactionsofUserBetween(userId, storeId,fromDate,toDate, page, size, sortBy, order);
		return new ResponseEntity<List<LedgerTransaction>>(allTransactionsofUser,HttpStatus.CREATED);
	}
	
	@GetMapping("/get-ledger-history/{userId}/{storeId}")
	public ResponseEntity<List<LedgerTransactionHistory>> getTransactionsHistory(
			@PathVariable String userId, 
			@PathVariable String storeId, 
			@RequestParam(required = false) int page,
			@RequestParam(required = false) int size,
			@RequestParam String sortBy,
			@RequestParam String order) {
		List<LedgerTransactionHistory> allTransactionsofUser = service
				.findAllTransactionsHistoryofUser(userId, storeId, page, size, sortBy, order);
		return new ResponseEntity<List<LedgerTransactionHistory>>(allTransactionsofUser,HttpStatus.CREATED);
	}
	
	@GetMapping("/get-ledger-history-between/{userId}/{storeId}")
	public ResponseEntity<List<LedgerTransactionHistory>> getTransactionsHistory(
			@PathVariable String userId, 
			@PathVariable String storeId,
			@RequestParam LocalDateTime fromDate, 
			@RequestParam LocalDateTime toDate,
			@RequestParam(required = false) int page,
			@RequestParam(required = false) int size,
			@RequestParam String sortBy,
			@RequestParam String order) {
		List<LedgerTransactionHistory> allTransactionsofUser = service
				.findAllTransactionsHistoryofUserBetween(userId, storeId,fromDate,toDate, page, size, sortBy, order);
		return new ResponseEntity<List<LedgerTransactionHistory>>(allTransactionsofUser,HttpStatus.CREATED);
	}
	
	
}
