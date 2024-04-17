package com.acintyo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.acintyo.customexceptions.TransactionNotFoundException;
import com.acintyo.dto.HeaderDto;
import com.acintyo.dto.HistoryDto;
import com.acintyo.dto.LedgerResponse;
import com.acintyo.dto.RequestDto;
import com.acintyo.dto.TransactionDto;
import com.acintyo.dto.UpdateRequestDto;
import com.acintyo.entity.LedgerHeader;
import com.acintyo.entity.LedgerTransaction;
import com.acintyo.entity.LedgerTransactionHistory;
import com.acintyo.repository.ILedgerHeaderRepository;
import com.acintyo.repository.ILedgerTransactionHistoryRepository;
import com.acintyo.repository.ILedgerTransactionRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
		log.info("newTransaction() method is invoked ");
		dto.convertToUpperCase();
		Optional<LedgerTransaction> transId = transactionRepository.findByTransId(dto.getTransId());
		if(transId.isPresent()) {
			log.debug("TransId is already Present. Throwing TransactionNotFoundException");
			throw new TransactionNotFoundException("TransId is already Present. Please send unique value");
		}
		//checking whether a user is already present or not
		Optional<LedgerHeader> optional = headerRepository.findByUserIdIgnoreCaseAndStoreIdIgnoreCase
				(dto.getUserId(), dto.getStoreId());
		LedgerHeader header = null;
		if (optional.isPresent()) {
			// fetching header
			header = optional.get();
			log.debug("User is already Present "+header);
		} else {
			// craete new Header
			header = new LedgerHeader(dto.getUserId(), dto.getStoreId(), 
					dto.getDetais(), 0.0, "CR");
			log.debug("User Not Found Creating new Hedaer  "+header);
		}
		String headerNote = header.getNote();
		String newNote = dto.getNote();
		header.setDescription(dto.getDetais());
		// note: old=CR and new=CR
		if (headerNote.equalsIgnoreCase("CR") && newNote.equalsIgnoreCase("CR")) {
			header.setNote(newNote);
			header.setHeaderAmt(header.getHeaderAmt() + dto.getAmount());
			log.debug("old Note = CR and new Note = CR ");
			log.debug("Header after Updation "+header);
		}
		// note: old=DR and new=CR
		else if (headerNote.equalsIgnoreCase("DR") && newNote.equalsIgnoreCase("DR")) {
			header.setNote(newNote);
			header.setHeaderAmt(header.getHeaderAmt() + dto.getAmount());
			log.debug("old Note = DR and new Note = DR ");
			log.debug("Header after Updation "+header);
		}
		// note: old=CR and new=DR
		else if (headerNote.equalsIgnoreCase("CR") && newNote.equalsIgnoreCase("DR")) {
			Double amount = header.getHeaderAmt() - dto.getAmount();
			header.setNote(amount > 0 ? "CR" : "DR");
			header.setHeaderAmt(Math.abs(amount));
			log.debug("old Note = CR and new Note = DR ");
			log.debug("Header after Updation "+header);
		}
		// note: old=DR and new=CR
		else if (headerNote.equalsIgnoreCase("DR") && newNote.equalsIgnoreCase("CR")) {
			Double amount = dto.getAmount() - header.getHeaderAmt();
			header.setNote(amount > 0 ? "CR" : "DR");
			header.setHeaderAmt(Math.abs(amount));
			log.debug("old Note = DR and new Note = CR ");
			log.debug("Header after Updation "+header);
		}
		log.debug("Transfering data from DTO class to LedgerTransaction entity class");
		LedgerTransaction ct = new LedgerTransaction(dto);
		ct.setHeader(header);
		log.debug("Header added to Transaction");
		
		LedgerTransactionHistory history = new LedgerTransactionHistory();
		history.setTransId(dto.getTransId());
		history.setUserId(dto.getUserId());
		history.setStoreId(dto.getStoreId());
		history.setDetais(dto.getDetais());
		history.setAmount(dto.getAmount());
		history.setNote(dto.getNote());
		history.setTransactionDate(dto.getTransactionDate());
		history.setInsertedBy(dto.getInsertedBy());
		// save the history
		historyRepository.save(history);
		log.debug("History details are saved "+history);
		// save the transaction
		transactionRepository.save(ct);
		log.debug("Transaction and Header Detais are saved using Transaction ");
		log.debug("Transaction Data "+ct);
		log.debug("Header Data "+header);
		//return the response
		return new LedgerResponse(true,"Transaction Successfull");
	}


	@Transactional
	@Override
	public LedgerResponse updateTransaction(UpdateRequestDto dto) {
		log.info(" updateTransaction() method is invoked ");
		dto.convertToUpperCase();
		Optional<LedgerTransaction> optionalTransaction = transactionRepository
				.findByRecordIdAndTransId(dto.getRecordId(), dto.getTransId());
		
		LedgerTransaction prevTrasaction = optionalTransaction.orElseThrow(()-> 
				new TransactionNotFoundException("Transaction Not found with given details"));
		log.debug("");
		//set updateBy to previous Transaction
		prevTrasaction.setUpdatedBy(dto.getUpdatedBy());
		
		// Corresponding LedgerHeader for Previous LedgerTransaction
		LedgerHeader header = headerRepository.findByUserIdIgnoreCaseAndStoreIdIgnoreCase
				(dto.getUserId(), dto.getStoreId()).get();
		
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

		LedgerTransactionHistory history = new LedgerTransactionHistory(
				dto.getTransId(), dto.getUserId(),
				dto.getStoreId(), dto.getDetais(), dto.getAmount(), 
				dto.getNote(), dto.getTransactionDate(),
				dto.getUpdatedBy());
		
		historyRepository.save(history);

		prevTrasaction.setAmount(dto.getAmount());
		prevTrasaction.setNote(dto.getNote());

		prevTrasaction = transactionRepository.save(prevTrasaction);
		// save the entity classes
		header = headerRepository.save(header);
		
		return new LedgerResponse(true,"Transaction Successfull");
	}

	@Override
	public HeaderDto findHeader(String userId, String storeId) {
		Optional<LedgerHeader> optional = headerRepository.findByUserIdIgnoreCaseAndStoreIdIgnoreCase(userId, storeId);
		if(optional.isPresent()) return new HeaderDto(optional.get());
		else return null;
	}

	@SuppressWarnings("null")
	@Override
	public List<TransactionDto> findAllTransactionsofUser
		(String userId, String storeId,int page, int size, String sortBy, String order) {
		List<LedgerTransaction> listOfTransactions = transactionRepository
				.findByHeaderUserIdAndStoreId(userId, storeId,PageRequest
				.of(page, size, order.equalsIgnoreCase("DESC")?Direction.DESC:Direction.ASC, sortBy));
//		list.forEach(tr->tr.setHeader(null));
		List<TransactionDto> transactionDto = null;
		listOfTransactions.forEach(e->transactionDto.add(new TransactionDto(e)));
		return transactionDto;
	}

	@SuppressWarnings("null")
	@Override
	public List<TransactionDto> findAllTransactionsofUserBetween
		(String userId, String storeId, LocalDateTime fromDate, LocalDateTime toDate,  
				int page, int size, String sortBy, String order) {
		List<LedgerTransaction> listOfTransactions = transactionRepository
				.findByHeaderUserIdAndStoreIdAndTransactionDateBetween(userId, storeId, fromDate, toDate, PageRequest
				.of(page, size, order.equalsIgnoreCase("DESC")?Direction.DESC:Direction.ASC, sortBy));
//		listOfTransactions.forEach(tr->tr.setHeader(null));
		List<TransactionDto> transactionDto = null;
		listOfTransactions.forEach(e->transactionDto.add(new TransactionDto(e)));
		return transactionDto;
	}

	@SuppressWarnings("null")
	@Override
	public List<HistoryDto> findAllTransactionsHistoryofUser
		(String userId, String storeId, int page, int size, String sortBy, String order) {
		List<LedgerTransactionHistory> history = historyRepository
				.findByUserIdAndStoreId(userId, storeId, PageRequest
				.of(page, size, order.equalsIgnoreCase("DESC")?Direction.DESC:Direction.ASC, sortBy));
		List<HistoryDto> historyDto = null;
		history.forEach(e->historyDto.add(new HistoryDto(e)));
		return historyDto;
	}

	@SuppressWarnings("null")
	@Override
	public List<HistoryDto> findAllTransactionsHistoryofUserBetween
		(String userId, String storeId, LocalDateTime fromDate, LocalDateTime toDate,
				int page, int size, String sortBy, String order) {
		List<LedgerTransactionHistory> history = historyRepository
					.findByUserIdAndStoreIdAndTransactionDateBetween(userId, storeId, fromDate, toDate,PageRequest
					.of(page, size, order.equalsIgnoreCase("DESC")?Direction.DESC:Direction.ASC, sortBy));		
		List<HistoryDto> historyDto = null;
		history.forEach(e->historyDto.add(new HistoryDto(e)));
		return historyDto;
	}
}
