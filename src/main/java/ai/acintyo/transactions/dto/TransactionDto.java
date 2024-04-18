package ai.acintyo.transactions.dto;

import java.time.LocalDateTime;

import ai.acintyo.transactions.entity.LedgerTransaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
	
	private Integer recordId;

	private String transId;
	
	private String storeId;

	private String detais;

	private Double amount;

	private String note;

	private LocalDateTime transactionDate;

	private String insertedBy;

	private LocalDateTime insertedOn;

	private String updatedBy;

	private LocalDateTime updatedOn;
	
	public TransactionDto(LedgerTransaction transaction) {
		this.recordId = transaction.getRecordId();
		this.transId = transaction.getTransId();
		this.storeId = transaction.getStoreId();
		this.detais = transaction.getDetais();
		this.amount = transaction.getAmount();
		this.note = transaction.getNote();
		this.transactionDate = transaction.getTransactionDate();
		this.insertedBy = transaction.getInsertedBy();
		this.insertedOn = transaction.getInsertedOn();
		this.updatedBy = transaction.getUpdatedBy();
		this.updatedOn = transaction.getUpdatedOn();
	}
}
