package com.acintyo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.acintyo.customexceptions.TransactionNotFoundException;
import com.acintyo.dto.LedgerResponse;
import com.acintyo.entity.LedgerHeader;
import com.acintyo.entity.LedgerTransaction;
import com.acintyo.entity.LedgerTransactionHistory;
import com.acintyo.repository.ILedgerHeaderRepository;
import com.acintyo.repository.ILedgerTransactionHistoryRepository;
import com.acintyo.repository.ILedgerTransactionRepository;

import jakarta.transaction.Transactional;

@Service
public class TransactionMgntServiceImpl implements ITransactionMgntService {

	@Value("${transaction.pagination.page-size}")
	private Integer pageSize;
	
	@Autowired
	private ILedgerHeaderRepository headerRepository;
	@Autowired
	private ILedgerTransactionRepository transactionRepository;
	@Autowired
	private ILedgerTransactionHistoryRepository historyRepository;

	@Override
	@Transactional
	public LedgerResponse doTransaction(LedgerTransaction ct, boolean newTrans) {
			if(newTrans) {
				newTransaction(ct);
			}
			else {
				Optional<LedgerTransaction> optionalTransaction = transactionRepository
						.findByRecordIdAndTransId(ct.getRecordId(), ct.getTransId());
				updateTransaction(ct, optionalTransaction.orElseThrow(()-> new TransactionNotFoundException("Transaction Not found with given details")));
			}
			LedgerResponse response = new LedgerResponse();
			response.setStatus(true);
			response.setMessage("Transaction completed successfully");
			return response;
	}

	private void newTransaction(LedgerTransaction ct) {
		Optional<LedgerHeader> optional = headerRepository.findByUserIdAndStoreId(ct.getUserId(), ct.getStoreId());
		LedgerHeader header = null;
		if (optional.isPresent()) {
			// fetching header
			header = optional.get();
		} else {
			// craete new Header
			header = new LedgerHeader(ct.getUserId(), ct.getStoreId(), ct.getDetais(), 0.0, "CR");
		}
		String headerNote = header.getNote();
		String newNote = ct.getNote();
		header.setDescription(ct.getDetais());
		// note: old=CR and new=CR
		if (headerNote.equalsIgnoreCase("CR") && newNote.equalsIgnoreCase("CR")) {
			header.setNote(newNote);
			header.setHeaderAmt(header.getHeaderAmt() + ct.getAmount());
		}
		// note: old=DR and new=CR
		else if (headerNote.equalsIgnoreCase("DR") && newNote.equalsIgnoreCase("DR")) {
			header.setNote(newNote);
			header.setHeaderAmt(header.getHeaderAmt() + ct.getAmount());
		}
		// note: old=CR and new=DR
		else if (headerNote.equalsIgnoreCase("CR") && newNote.equalsIgnoreCase("DR")) {
			Double amount = header.getHeaderAmt() - ct.getAmount();
			header.setNote(amount > 0 ? "CR" : "DR");
			header.setHeaderAmt(Math.abs(amount));
		}
		// note: old=DR and new=CR
		else if (headerNote.equalsIgnoreCase("DR") && newNote.equalsIgnoreCase("CR")) {
			Double amount = ct.getAmount() - header.getHeaderAmt();
			header.setNote(amount > 0 ? "CR" : "DR");
			header.setHeaderAmt(Math.abs(amount));
		}
		// all header to transactionF
		ct.setHeader(header);

		LedgerTransactionHistory history = new LedgerTransactionHistory();
		history.setTransId(ct.getTransId());
		history.setUserId(ct.getUserId());
		history.setStoreId(ct.getStoreId());
		history.setDetais(ct.getDetais());
		history.setAmount(ct.getAmount());
		history.setNote(ct.getNote());
		history.setTransactionDate(ct.getTransactionDate());
		history.setInsertedBy(ct.getInsertedBy());
		// save the history
		historyRepository.save(history);
		// save the transaction
		transactionRepository.save(ct);
		// update the header
		header = headerRepository.save(header);
		System.out.println("\n header : " + header + "\n");

	}

	private void updateTransaction(LedgerTransaction ct, LedgerTransaction prevTrasaction) {
		// Previous LedgerTransaction
		System.out.println("\n before update previous Transaction : " + prevTrasaction + "\n");
		
		// Corresponding LedgerHeader for Previous LedgerTransaction
		LedgerHeader header = headerRepository.findByUserIdAndStoreId(ct.getUserId(), ct.getStoreId()).get();
		System.out.println("\nbefore update Header : " + header + "\n");
		
		// header amount
		double headerAmt = header.getNote().equalsIgnoreCase("CR") ? header.getHeaderAmt() : (-header.getHeaderAmt());
		
		// note: old=CR and new=CR
		if (prevTrasaction.getNote().equalsIgnoreCase("CR") && ct.getNote().equalsIgnoreCase("CR")) {
			headerAmt = headerAmt + ct.getAmount() - prevTrasaction.getAmount();
		}
		// note: old=DR and new=DR
		else if (prevTrasaction.getNote().equalsIgnoreCase("DR") && ct.getNote().equalsIgnoreCase("DR")) {
			headerAmt = headerAmt + prevTrasaction.getAmount() - ct.getAmount();
		}
		// note: old=CR and new=DR
		else if (prevTrasaction.getNote().equalsIgnoreCase("CR") && ct.getNote().equalsIgnoreCase("DR")) {
			headerAmt = headerAmt - prevTrasaction.getAmount() - ct.getAmount();
		}
		// note: old=DR and new=CR
		else if (prevTrasaction.getNote().equalsIgnoreCase("DR") && ct.getNote().equalsIgnoreCase("CR")) {
			headerAmt = headerAmt + prevTrasaction.getAmount() + ct.getAmount();
		}

		header.setNote(headerAmt >= 0.0 ? "CR" : "DR");
		header.setHeaderAmt(Math.abs(headerAmt));

		LedgerTransactionHistory history = new LedgerTransactionHistory(ct.getTransId(), ct.getUserId(),
				ct.getStoreId(), ct.getDetais(), ct.getAmount(), ct.getNote(), ct.getTransactionDate(),
				ct.getUpdatedBy());
		
		historyRepository.save(history);

		prevTrasaction.setAmount(ct.getAmount());
		prevTrasaction.setNote(ct.getNote());

		prevTrasaction = transactionRepository.save(prevTrasaction);

		// save the entity classes
		header = headerRepository.save(header);
	}

	@Override
	public LedgerHeader findHeader(String userId, String storeId) {
		Optional<LedgerHeader> optional = headerRepository.findByUserIdAndStoreId(userId, storeId);
		if(optional.isPresent()) return optional.get();
		else return null;
	}

	@Override
	public List<LedgerTransaction> findAllTransactionsofUser(String userId, String storeId, int pageNo) {
		Pageable pageable = PageRequest.of(pageNo-1,pageSize);
		List<LedgerTransaction> list = transactionRepository
				.findByUserIdAndStoreIdOrderByTransactionDateDesc(userId, storeId,pageable);
		list.forEach(tr->tr.setHeader(null));
		return list;
	}

	@Override
	public List<LedgerTransaction> findAllTransactionsofUserBetween(String userId, String storeId,
			LocalDateTime fromDate, LocalDateTime toDate, int pageNo) {
		Pageable pageable = PageRequest.of(pageNo-1,pageSize);
		List<LedgerTransaction> listOfTransactions = transactionRepository
					.findByUserIdAndStoreIdAndTransactionDateBetweenOrderByTransactionDateDesc
						(userId, storeId, fromDate, toDate, pageable);
		listOfTransactions.forEach(tr->tr.setHeader(null));
		return listOfTransactions;
	}

	@Override
	public List<LedgerTransactionHistory> findAllTransactionsHistoryofUser(String userId, String storeId, int pageNo) {
		Pageable pageable = PageRequest.of(pageNo-1,pageSize);
		List<LedgerTransactionHistory> history = historyRepository
				.findByUserIdAndStoreIdOrderByTransactionDate(userId, storeId,pageable);
		return history;
	}

	@Override
	public List<LedgerTransactionHistory> findAllTransactionsHistoryofUserBetween(String userId, String storeId,
			LocalDateTime fromDate, LocalDateTime toDate, int pageNo) {
		Pageable pageable = PageRequest.of(pageNo-1,pageSize);
		List<LedgerTransactionHistory> history = historyRepository
					.findByUserIdAndStoreIdAndTransactionDateBetweenOrderByTransactionDate
						(userId, storeId, fromDate, toDate, pageable);
		return history;
	}
}
