package com.acintyo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.acintyo.customexceptions.TransactionNotFoundException;
import com.acintyo.dto.LedgerResponse;
import com.acintyo.dto.RequestDto;
import com.acintyo.dto.UpdateRequestDto;
import com.acintyo.entity.LedgerHeader;
import com.acintyo.entity.LedgerTransaction;
import com.acintyo.entity.LedgerTransactionHistory;
import com.acintyo.repository.ILedgerHeaderRepository;
import com.acintyo.repository.ILedgerTransactionHistoryRepository;
import com.acintyo.repository.ILedgerTransactionRepository;

import jakarta.transaction.Transactional;

@Service
public class TransactionMgntServiceImpl implements ITransactionMgntService {

	@Autowired
	private ILedgerHeaderRepository headerRepository;
	@Autowired
	private ILedgerTransactionRepository transactionRepository;
	@Autowired
	private ILedgerTransactionHistoryRepository historyRepository;

	
	@Transactional
	@Override
	public  LedgerResponse newTransaction(RequestDto dto) {
		//checking whether TransId is already present or Not
		Optional<LedgerTransaction> transId = transactionRepository.findByTransId(dto.getTransId().toUpperCase());
		if(transId.isPresent()) {
			throw new TransactionNotFoundException("TransId is already Present. Please send unique value");
		}
		//checking whether a user is already present or not
		Optional<LedgerHeader> optional = headerRepository.findByUserIdIgnoreCaseAndStoreIdIgnoreCase(dto.getUserId().toUpperCase(), dto.getStoreId().toUpperCase());
		LedgerHeader header = null;
		if (optional.isPresent()) {
			// fetching header
			header = optional.get();
		} else {
			// craete new Header
			header = new LedgerHeader(dto.getUserId().toUpperCase(), dto.getStoreId().toUpperCase(), dto.getDetais(), 0.0, "CR");
		}
		String headerNote = header.getNote();
		String newNote = dto.getNote();
		header.setDescription(dto.getDetais());
		// note: old=CR and new=CR
		if (headerNote.equalsIgnoreCase("CR") && newNote.equalsIgnoreCase("CR")) {
			header.setNote(newNote);
			header.setHeaderAmt(header.getHeaderAmt() + dto.getAmount());
		}
		// note: old=DR and new=CR
		else if (headerNote.equalsIgnoreCase("DR") && newNote.equalsIgnoreCase("DR")) {
			header.setNote(newNote);
			header.setHeaderAmt(header.getHeaderAmt() + dto.getAmount());
		}
		// note: old=CR and new=DR
		else if (headerNote.equalsIgnoreCase("CR") && newNote.equalsIgnoreCase("DR")) {
			Double amount = header.getHeaderAmt() - dto.getAmount();
			header.setNote(amount > 0 ? "CR" : "DR");
			header.setHeaderAmt(Math.abs(amount));
		}
		// note: old=DR and new=CR
		else if (headerNote.equalsIgnoreCase("DR") && newNote.equalsIgnoreCase("CR")) {
			Double amount = dto.getAmount() - header.getHeaderAmt();
			header.setNote(amount > 0 ? "CR" : "DR");
			header.setHeaderAmt(Math.abs(amount));
		}
		// all header to transactionF
		LedgerTransaction ct = new LedgerTransaction(dto);
		ct.setHeader(header);

		LedgerTransactionHistory history = new LedgerTransactionHistory();
		history.setTransId(dto.getTransId().toUpperCase());
		history.setUserId(dto.getUserId().toUpperCase());
		history.setStoreId(dto.getStoreId().toUpperCase());
		history.setDetais(dto.getDetais());
		history.setAmount(dto.getAmount());
		history.setNote(dto.getNote().toUpperCase());
		history.setTransactionDate(dto.getTransactionDate());
		history.setInsertedBy(dto.getInsertedBy());
		// save the history
		historyRepository.save(history);
		// save the transaction
		transactionRepository.save(ct);
		// update the header
		header = headerRepository.save(header);
		System.out.println("\n header : " + header + "\n");

		//return the response
		return new LedgerResponse(true,"Transaction Successfull");
	}


	@Transactional
	@Override
	public LedgerResponse updateTransaction(UpdateRequestDto dto) {
		Optional<LedgerTransaction> optionalTransaction = transactionRepository
				.findByRecordIdAndTransId(dto.getRecordId(), dto.getTransId().toUpperCase());
		
		LedgerTransaction prevTrasaction = optionalTransaction.orElseThrow(()-> 
				new TransactionNotFoundException("Transaction Not found with given details"));
		
		//set updateBy to previous Transaction
		prevTrasaction.setUpdatedBy(dto.getUpdatedBy());
		
		// Corresponding LedgerHeader for Previous LedgerTransaction
		LedgerHeader header = headerRepository.findByUserIdIgnoreCaseAndStoreIdIgnoreCase(dto.getUserId().toUpperCase(), dto.getStoreId().toUpperCase()).get();
		
		// header amount
		double headerAmt = header.getNote().equalsIgnoreCase("CR") ? header.getHeaderAmt() : (-header.getHeaderAmt());
		
		// note: old=CR and new=CR
		if (prevTrasaction.getNote().equalsIgnoreCase("CR") && dto.getNote().equalsIgnoreCase("CR")) {
			headerAmt = headerAmt + dto.getAmount() - prevTrasaction.getAmount();
		}
		// note: old=DR and new=DR
		else if (prevTrasaction.getNote().equalsIgnoreCase("DR") && dto.getNote().equalsIgnoreCase("DR")) {
			headerAmt = headerAmt + prevTrasaction.getAmount() - dto.getAmount();
		}
		// note: old=CR and new=DR
		else if (prevTrasaction.getNote().equalsIgnoreCase("CR") && dto.getNote().equalsIgnoreCase("DR")) {
			headerAmt = headerAmt - prevTrasaction.getAmount() - dto.getAmount();
		}
		// note: old=DR and new=CR
		else if (prevTrasaction.getNote().equalsIgnoreCase("DR") && dto.getNote().equalsIgnoreCase("CR")) {
			headerAmt = headerAmt + prevTrasaction.getAmount() + dto.getAmount();
		}

		header.setNote(headerAmt >= 0.0 ? "CR" : "DR");
		header.setHeaderAmt(Math.abs(headerAmt));

		LedgerTransactionHistory history = new LedgerTransactionHistory(dto.getTransId().toUpperCase(), dto.getUserId().toUpperCase(),
				dto.getStoreId().toUpperCase(), dto.getDetais(), dto.getAmount(), dto.getNote().toUpperCase(), dto.getTransactionDate(),
				dto.getUpdatedBy());
		
		historyRepository.save(history);

		prevTrasaction.setAmount(dto.getAmount());
		prevTrasaction.setNote(dto.getNote().toUpperCase());

		prevTrasaction = transactionRepository.save(prevTrasaction);
		// save the entity classes
		header = headerRepository.save(header);
		
		return new LedgerResponse(true,"Transaction Successfull");
	}

	@Override
	public LedgerHeader findHeader(String userId, String storeId) {
		Optional<LedgerHeader> optional = headerRepository.findByUserIdIgnoreCaseAndStoreIdIgnoreCase(userId, storeId);
		if(optional.isPresent()) return optional.get();
		else return null;
	}

	@Override
	public List<LedgerTransaction> findAllTransactionsofUser
		(String userId, String storeId,int page, int size, String sortBy, String order) {
		List<LedgerTransaction> list = transactionRepository
				.findByHeaderUserIdAndStoreId(userId, storeId,PageRequest
				.of(page, size, order.equalsIgnoreCase("DESC")?Direction.DESC:Direction.ASC, sortBy));
		list.forEach(tr->tr.setHeader(null));
		return list;
	}

	@Override
	public List<LedgerTransaction> findAllTransactionsofUserBetween
		(String userId, String storeId, LocalDateTime fromDate, LocalDateTime toDate,  
				int page, int size, String sortBy, String order) {
		List<LedgerTransaction> listOfTransactions = transactionRepository
				.findByHeaderUserIdAndStoreIdAndTransactionDateBetween(userId, storeId, fromDate, toDate, PageRequest
				.of(page, size, order.equalsIgnoreCase("DESC")?Direction.DESC:Direction.ASC, sortBy));
		listOfTransactions.forEach(tr->tr.setHeader(null));
		return listOfTransactions;
	}

	@Override
	public List<LedgerTransactionHistory> findAllTransactionsHistoryofUser
		(String userId, String storeId, int page, int size, String sortBy, String order) {
		List<LedgerTransactionHistory> history = historyRepository
				.findByUserIdAndStoreId(userId, storeId, PageRequest
				.of(page, size, order.equalsIgnoreCase("DESC")?Direction.DESC:Direction.ASC, sortBy));
		return history;
	}

	@Override
	public List<LedgerTransactionHistory> findAllTransactionsHistoryofUserBetween
		(String userId, String storeId, LocalDateTime fromDate, LocalDateTime toDate,
				int page, int size, String sortBy, String order) {
		List<LedgerTransactionHistory> history = historyRepository
					.findByUserIdAndStoreIdAndTransactionDateBetween(userId, storeId, fromDate, toDate,PageRequest
					.of(page, size, order.equalsIgnoreCase("DESC")?Direction.DESC:Direction.ASC, sortBy));		
		return history;
	}
}
