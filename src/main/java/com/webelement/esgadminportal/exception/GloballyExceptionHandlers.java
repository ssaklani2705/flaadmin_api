package com.webelement.esgadminportal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.webelement.esgadminportal.common.ResponseApi;

@RestControllerAdvice
public class GloballyExceptionHandlers {

	@ExceptionHandler(PasswordValidationException.class)
	public ResponseEntity<ResponseApi<String>> handleClientValidationException(PasswordValidationException ex) {

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseApi<>(false, ex.getMessage(), null));
	}
}
