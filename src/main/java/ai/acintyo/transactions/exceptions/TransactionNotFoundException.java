package ai.acintyo.transactions.exceptions;

@SuppressWarnings("serial")
public class TransactionNotFoundException extends RuntimeException{

	public TransactionNotFoundException() {
		super();
	}

	public TransactionNotFoundException(String message) {
		super(message);
	}
}
