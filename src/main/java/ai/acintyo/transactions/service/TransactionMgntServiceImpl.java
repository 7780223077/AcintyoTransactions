package ai.acintyo.transactions.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import ai.acintyo.transactions.dto.HeaderDto;
import ai.acintyo.transactions.dto.HistoryDto;
import ai.acintyo.transactions.dto.LedgerResponse;
import ai.acintyo.transactions.dto.RequestDto;
import ai.acintyo.transactions.dto.TransactionDto;
import ai.acintyo.transactions.dto.UpdateRequestDto;
import ai.acintyo.transactions.entity.LedgerHeader;
import ai.acintyo.transactions.entity.LedgerTransaction;
import ai.acintyo.transactions.entity.LedgerTransactionHistory;
import ai.acintyo.transactions.exceptions.TransactionFoundException;
import ai.acintyo.transactions.exceptions.TransactionNotFoundException;
import ai.acintyo.transactions.mapper.TransactionMapper;
import ai.acintyo.transactions.repository.ILedgerHeaderRepository;
import ai.acintyo.transactions.repository.ILedgerTransactionHistoryRepository;
import ai.acintyo.transactions.repository.ILedgerTransactionRepository;
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
		//Converting data into upper case
		dto.convertToUpperCase();
		Optional<LedgerTransaction> transId = transactionRepository.findByTransId(dto.getTransId());
		if(transId.isPresent()) {
			log.debug("TransId is already Present. Throwing TransactionFoundException");
			throw new TransactionFoundException("TransId is already Present. Please send unique value");
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
			header = new LedgerHeader(dto.getUserId(), dto.getStoreId(), dto.getDetais(), 0.0, "CR");
			log.debug("User Not Found Creating new Hedaer  "+header);
		}
		String headerNote = header.getNote();
		String newNote = dto.getNote();
		header.setDescription(dto.getDetais());
		// note: old=CR and new=CR
		if (headerNote.equalsIgnoreCase("CR") && newNote.equalsIgnoreCase("CR")) {
			//header.setNote(newNote);
			header.setHeaderAmt(header.getHeaderAmt() + dto.getAmount());
			log.debug("old Note = CR and new Note = CR ");
			log.debug("Header after Updation "+header);
		}
		// note: old=DR and new=DR
		else if (headerNote.equalsIgnoreCase("DR") && newNote.equalsIgnoreCase("DR")) {
			//header.setNote(newNote);
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
		LedgerTransaction ct = TransactionMapper.requestDtoToTransactionEntity(dto);
		ct.setHeader(header);
		log.debug("Header added to Transaction");
		
		LedgerTransactionHistory history = TransactionMapper.requestDtoToHistoryEntity(dto);
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

		LedgerTransactionHistory history = TransactionMapper.updateRequestDtoToHistoryEntity(dto);
		
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
		if(optional.isPresent()) 
			return TransactionMapper.entityToHeaderDto(optional.get());
		else return new HeaderDto();
	}

	@Override
	public List<TransactionDto> findAllTransactionsofUser
		(String userId, String storeId,int page, int size, String sortBy, String order) {
		List<LedgerTransaction> listOfTransactions = transactionRepository
				.findByHeaderUserIdAndStoreId(userId, storeId,PageRequest
				.of(page, size, order.equalsIgnoreCase("DESC")?Direction.DESC:Direction.ASC, sortBy));
//		list.forEach(tr->tr.setHeader(null));
		List<TransactionDto> transactionDto =  new ArrayList<>();
		listOfTransactions.forEach(e->transactionDto.add(new TransactionDto(e)));
		return transactionDto;
	}

	@Override
	public List<TransactionDto> findAllTransactionsofUserBetween
		(String userId, String storeId, LocalDateTime fromDate, LocalDateTime toDate,  
				int page, int size, String sortBy, String order) {
		List<LedgerTransaction> listOfTransactions = transactionRepository
				.findByHeaderUserIdAndStoreIdAndTransactionDateBetween(userId, storeId, fromDate, toDate, PageRequest
				.of(page, size, order.equalsIgnoreCase("DESC")?Direction.DESC:Direction.ASC, sortBy));
//		listOfTransactions.forEach(tr->tr.setHeader(null));
		List<TransactionDto> transactionDto =  new ArrayList<>();
		listOfTransactions.forEach(e->transactionDto.add(new TransactionDto(e)));
		return transactionDto;
	}

	@Override
	public List<HistoryDto> findAllTransactionsHistoryofUser
		(String userId, String storeId, int page, int size, String sortBy, String order) {
		List<LedgerTransactionHistory> history = historyRepository
				.findByUserIdAndStoreId(userId, storeId, PageRequest
				.of(page, size, order.equalsIgnoreCase("DESC")?Direction.DESC:Direction.ASC, sortBy));
		List<HistoryDto> historyDto =  new ArrayList<>();
		history.forEach(e->historyDto.add(new HistoryDto(e)));
		return historyDto;
	}

	@Override
	public List<HistoryDto> findAllTransactionsHistoryofUserBetween
		(String userId, String storeId, LocalDateTime fromDate, LocalDateTime toDate,
				int page, int size, String sortBy, String order) {
		List<LedgerTransactionHistory> history = historyRepository
					.findByUserIdAndStoreIdAndTransactionDateBetween(userId, storeId, fromDate, toDate,PageRequest
					.of(page, size, order.equalsIgnoreCase("DESC")?Direction.DESC:Direction.ASC, sortBy));		
		List<HistoryDto> historyDto = new ArrayList<>();
		history.forEach(e->historyDto.add(new HistoryDto(e)));
		return historyDto;
	}
}
