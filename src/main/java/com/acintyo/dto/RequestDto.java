package com.acintyo.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RequestDto {

	@NotBlank(message = "{validation.notblank.trans-id}")
	private String transId;

	@NotBlank(message = "{validation.notblank.user-id}")
	private String userId;

	@NotBlank(message = "{validation.notblank.store-id}")
	private String storeId;

	@NotBlank(message = "{validation.notblank.details}")
	private String detais;

	@NotNull(message = "{validation.notblank.amount}")
	@Min(value = 0, message = "{validation.notblank.amount}")
	private Double amount;

	@NotBlank(message = "{validation.notblank.note}")
	@Pattern(regexp = "(?i)^(CR|DR)$", message = "{validation.pattern.note}")
	private String note;

	@NotNull(message = "{validation.notnull.transaction-date}")
	private LocalDateTime transactionDate;

	@NotBlank(message = "{validation.notblank.inserted-by}")
	private String insertedBy;
	
	public void convertToUpperCase() {
		this.transId = this.transId.toUpperCase();
		this.userId = this.userId.toUpperCase();
		this.storeId = this.storeId.toUpperCase();
		this.note = this.note.toUpperCase();
		this.insertedBy = this.insertedBy.toUpperCase();
	}
}
