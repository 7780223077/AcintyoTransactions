package ai.acintyo.transactions.dto;

import java.time.LocalDateTime;

import ai.acintyo.transactions.entity.LedgerTransactionHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryDto {
	
	private Integer recordId;
	
	private String transId;
	
	private String userId;
	
	private String storeId;
	
	private String detais;
	
	private Double amount;
	
	private String note;
	
	private LocalDateTime transactionDate;
	
	private String insertedBy;

	private LocalDateTime insertDate;
	
	public HistoryDto(LedgerTransactionHistory history) {
		this.recordId = history.getRecordId();
		this.transId = history.getTransId();
		this.userId = history.getUserId();
		this.storeId = history.getStoreId();
		this.detais = history.getDetais();
		this.amount = history.getAmount();
		this.note = history.getNote();
		this.transactionDate = history.getTransactionDate();
		this.insertedBy = history.getInsertedBy();
		this.insertDate = history.getInsertDate();
	}
}
