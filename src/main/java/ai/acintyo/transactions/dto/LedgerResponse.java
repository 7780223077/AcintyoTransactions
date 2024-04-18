package ai.acintyo.transactions.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@SuppressWarnings("serial")
@AllArgsConstructor
@NoArgsConstructor
public class LedgerResponse implements Serializable{
	private boolean status;
	private String message;
}
