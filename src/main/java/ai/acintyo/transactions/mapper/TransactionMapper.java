package ai.acintyo.transactions.mapper;

import ai.acintyo.transactions.dto.HeaderDto;
import ai.acintyo.transactions.dto.RequestDto;
import ai.acintyo.transactions.dto.UpdateRequestDto;
import ai.acintyo.transactions.entity.LedgerHeader;
import ai.acintyo.transactions.entity.LedgerTransaction;
import ai.acintyo.transactions.entity.LedgerTransactionHistory;

public class TransactionMapper {

	public static HeaderDto entityToHeaderDto(LedgerHeader header) {
		return HeaderDto.builder().userId(header.getUserId()).storeId(header.getStoreId())
				.description(header.getDescription()).headerAmt(header.getHeaderAmt()).note(header.getNote())
				.insertedOn(header.getInsertedOn()).updatedOn(header.getUpdatedOn()).build();
	}

	public static LedgerTransactionHistory requestDtoToHistoryEntity(RequestDto dto) {
		return LedgerTransactionHistory.builder().transId(dto.getTransId()).userId(dto.getUserId())
				.storeId(dto.getStoreId()).detais(dto.getDetais()).amount(dto.getAmount()).note(dto.getNote())
				.transactionDate(dto.getTransactionDate()).insertedBy(dto.getInsertedBy()).build();
	}

	public static LedgerTransactionHistory updateRequestDtoToHistoryEntity(UpdateRequestDto dto) {
		return LedgerTransactionHistory.builder().transId(dto.getTransId()).userId(dto.getUserId())
				.storeId(dto.getStoreId()).detais(dto.getDetais()).amount(dto.getAmount()).note(dto.getNote())
				.transactionDate(dto.getTransactionDate()).insertedBy(dto.getUpdatedBy()).build();
	}

	// constructor for new Transaction
	public static LedgerTransaction requestDtoToTransactionEntity(RequestDto request) {
			return LedgerTransaction.builder()
					.transId(request.getTransId())
					.storeId(request.getStoreId())
					.detais(request.getDetais())
					.amount(request.getAmount())
					.note(request.getNote())
					.transactionDate(request.getTransactionDate())
					.insertedBy(request.getInsertedBy())
					.build();
		}

}
