package ai.acintyo.transactions.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HeaderDto {

	private String userId;
	
	private String storeId;
	
	private String description;
	
	private Double headerAmt;
	
	private String note;

	private LocalDateTime insertedOn;
	
	private LocalDateTime updatedOn;

}
