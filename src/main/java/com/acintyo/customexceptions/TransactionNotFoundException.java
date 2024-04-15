package com.acintyo.customexceptions;

@SuppressWarnings("serial")
public class TransactionNotFoundException extends RuntimeException{

	public TransactionNotFoundException() {
		super();
	}

	public TransactionNotFoundException(String message) {
		super(message);
	}
}
