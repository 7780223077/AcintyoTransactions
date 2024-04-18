package ai.acintyo.transactions.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("serial")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "LEDGER_HEADER")
public class LedgerHeader implements Serializable{
	
	@Id
	@Column(length = 30)
	@NonNull
	private String userId;
	
	@NonNull
	@Column
	private String storeId;
	
	@NonNull
	@Column(length = 30)
	private String description;
	
	@NonNull
	private Double headerAmt;
	
	@NonNull
	@Column(length = 2)
	private String note;

	@CreationTimestamp
	private LocalDateTime insertedOn;
	
	@UpdateTimestamp
	private LocalDateTime updatedOn;
}
