package ai.acintyo.transactions.controller;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ai.acintyo.transactions.dto.LedgerResponse;
import ai.acintyo.transactions.exceptions.TransactionFoundException;
import ai.acintyo.transactions.exceptions.TransactionNotFoundException;

@RestControllerAdvice
public class TransactionErrorHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<LedgerResponse> handleInvalidArgumentException(MethodArgumentNotValidException ex){
		HashMap<String, String> errorMap = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error-> {
			errorMap.put(error.getField(), error.getDefaultMessage());
		});
		
		LedgerResponse response = new LedgerResponse(false,errorMap.toString());
		return new ResponseEntity<LedgerResponse>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<LedgerResponse> handleException(HttpMessageNotReadableException ex){
		LedgerResponse response = new LedgerResponse(false,"Invalid Amount or Invalid date {Date format : YYYY-MM-DDTHH:MM:SS}");
		return new ResponseEntity<LedgerResponse>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(TransactionNotFoundException.class)
	public ResponseEntity<LedgerResponse> handleTransactionNotFoundException(TransactionNotFoundException ex){
		LedgerResponse response = new LedgerResponse(false,ex.getMessage());
		return new ResponseEntity<LedgerResponse>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(TransactionFoundException.class)
	public ResponseEntity<LedgerResponse> handleTransactionFoundException(TransactionFoundException ex){
		LedgerResponse response = new LedgerResponse(false,ex.getMessage());
		return new ResponseEntity<LedgerResponse>(response, HttpStatus.BAD_REQUEST);
	}
	
	
}
