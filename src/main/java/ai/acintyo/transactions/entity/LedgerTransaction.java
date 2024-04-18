package ai.acintyo.transactions.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import ai.acintyo.transactions.dto.RequestDto;
import ai.acintyo.transactions.dto.UpdateRequestDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "LEDGER_TRANSACTION")
public class LedgerTransaction implements Serializable{
	
	@Id
	@SequenceGenerator(name = "id", sequenceName = "trans_record_id", initialValue = 101 , allocationSize = 1)
	@GeneratedValue(generator = "id", strategy = GenerationType.SEQUENCE)
	private Integer recordId;

	@Column(length = 30)
	private String transId;
	
	@Column(length = 30)
	private String storeId;

	@Column(length = 30)
	private String detais;

	private Double amount;

	@Column(length = 30)
	private String note;

	private LocalDateTime transactionDate;

	@Column(length = 30)
	private String insertedBy;

	@CreationTimestamp
	private LocalDateTime insertedOn;

	@Column(length = 30)
	private String updatedBy;

	@UpdateTimestamp
	private LocalDateTime updatedOn;

	@ManyToOne(targetEntity = LedgerHeader.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "user_Id", referencedColumnName = "userId")
	private LedgerHeader header;

	
	//constructor for new Transaction
	public LedgerTransaction(RequestDto request) {
		super();
		this.transId=request.getTransId().toUpperCase();
		this.storeId=request.getStoreId().toUpperCase();
		this.detais=request.getDetais();
		this.amount=request.getAmount();
		this.note=request.getNote().toUpperCase();
		this.transactionDate=request.getTransactionDate();
		this.insertedBy=request.getInsertedBy();
	}
	
	//constructor for update Transaction
	public LedgerTransaction(UpdateRequestDto request) {
		super();
		this.recordId=request.getRecordId();
		this.transId=request.getTransId().toUpperCase();
		this.storeId=request.getStoreId().toUpperCase();
		this.detais=request.getDetais();
		this.amount=request.getAmount();
		this.note=request.getNote().toUpperCase();
		this.transactionDate=request.getTransactionDate();
		this.updatedBy=request.getUpdatedBy();
	}

	@Override
	public String toString() {
		return "LedgerTransaction [recordId=" + recordId + ", transId=" + transId + ", storeId=" + storeId + ", detais="
				+ detais + ", amount=" + amount + ", note=" + note + ", transactionDate=" + transactionDate
				+ ", insertedBy=" + insertedBy + ", insertedOn=" + insertedOn + ", updatedBy=" + updatedBy
				+ ", updatedOn=" + updatedOn + "]";
	}
}