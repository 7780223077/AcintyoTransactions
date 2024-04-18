package ai.acintyo.transactions.exceptions;

@SuppressWarnings("serial")
public class TransactionFoundException extends RuntimeException {

	public TransactionFoundException() {
		super();
	}

	public TransactionFoundException(String message) {
		super(message);
	}

}
