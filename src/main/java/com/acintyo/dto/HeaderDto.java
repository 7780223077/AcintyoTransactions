package com.acintyo.dto;

import java.time.LocalDateTime;

import com.acintyo.entity.LedgerHeader;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeaderDto {

	private String userId;
	
	private String storeId;
	
	private String description;
	
	private Double headerAmt;
	
	private String note;

	private LocalDateTime insertedOn;
	
	private LocalDateTime updatedOn;

	public HeaderDto(LedgerHeader header) {
		this.userId = header.getUserId();
		this.storeId = header.getStoreId();
		this.description = header.getDescription();
		this.headerAmt = header.getHeaderAmt();
		this.note = header.getNote();
		this.insertedOn = header.getInsertedOn();
		this.updatedOn = header.getUpdatedOn();
	}
}
