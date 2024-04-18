package ai.acintyo.transactions.entity;


import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "LEDGER_TRANSACTION_HISTORY")
public class LedgerTransactionHistory {
	
	@Id
	@SequenceGenerator(name = "GEN", sequenceName = "SEQ_TRANS_HISTORY", initialValue = 101, allocationSize = 1)
	@GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
	private Integer recordId;
	
	@NonNull
	@Column(length = 30)
	private String transId;
	
	@NonNull
	@Column(length = 30)
	private String userId;
	
	@NonNull
	@Column(length = 30)
	private String storeId;
	
	@NonNull
	@Column(length = 30)
	private String detais;
	
	@NonNull
	private Double amount;
	
	@NonNull
	@Column(length = 30)
	private String note;
	
	@NonNull
	private LocalDateTime transactionDate;
	
	@NonNull
	@Column(length = 30)
	private String insertedBy;

	@CreationTimestamp
	private LocalDateTime insertDate;

}

