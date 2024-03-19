package com.dreamsol.exceptions;

import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.PersistenceException;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dreamsol.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler 
{
	public static ApiResponse apiResponse = new ApiResponse();
	public static final boolean NOT_SUCCESS = false;
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String,String>> methodArgumentNotFoundExceptionHandler(MethodArgumentNotValidException ex)
	{
		Map<String,String> errorResponseMap = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error)->{
			String fieldName = ((FieldError)error).getField();
			String message = error.getDefaultMessage();
			errorResponseMap.put(fieldName, message);
		});
		return new ResponseEntity<Map<String,String>>(errorResponseMap,HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<ApiResponse> nullPointerExceptionHandler(NullPointerException e)
	{
		apiResponse.setMessage(e.getMessage());
		apiResponse.setSuccess(false);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
	}
	@ExceptionHandler(InvalidInputFormat.class)
	public ResponseEntity<ApiResponse> invalidInputFormat(InvalidInputFormat e)
	{
		apiResponse.setMessage(e.getMessage());
		apiResponse.setSuccess(false);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
	}
	@ExceptionHandler(ResourceAlreadyExistException.class)
	public ResponseEntity<ApiResponse> resourceAlreadyExistExceptionHandler(ResourceAlreadyExistException e)
	{
		apiResponse.setMessage(e.getMessage());
		apiResponse.setSuccess(false);
		return new ResponseEntity<ApiResponse>(apiResponse,HttpStatus.CONFLICT);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse> resourceNotFoundExceptionHandler(ResourceNotFoundException ex)
	{
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message,false);
		return new ResponseEntity<ApiResponse>(apiResponse,HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidFileFormatException.class)
	public ResponseEntity<ApiResponse> invalidFileFormatExceptionHandler(InvalidFileFormatException e)
	{
		apiResponse.setMessage(e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
	}

	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<ApiResponse> dataAccessExceptionHandler(DataAccessException e)
	{
		apiResponse.setMessage(e.getMessage());
		apiResponse.setSuccess(false);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
	}

	@ExceptionHandler(PersistenceException.class)
	public ResponseEntity<ApiResponse> persistenceExceptionHandler(PersistenceException e)
	{
		apiResponse.setMessage(e.getMessage());
		apiResponse.setSuccess(false);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
	}
	@ExceptionHandler(NoSuchFileException.class)
	public ResponseEntity<ApiResponse> noSuchFileExceptionHandler(NoSuchFileException e)
	{
		apiResponse.setMessage(e.getMessage());
		apiResponse.setSuccess(false);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
	}
}
