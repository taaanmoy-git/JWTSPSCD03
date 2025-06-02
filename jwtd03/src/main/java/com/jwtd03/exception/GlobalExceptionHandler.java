package com.jwtd03.exception;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletResponse;



@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorMessage> exceptionHandler(BadCredentialsException ex) {
		logger.warn("Authentication failed: {}", ex.getMessage());
		ErrorMessage errorMessage = new ErrorMessage(
                HttpServletResponse.SC_BAD_REQUEST,
                "Access is denied. Invalid Username or Password!",
                LocalDateTime.now().toString()
        );

		return new ResponseEntity<ErrorMessage>(errorMessage, HttpStatus.UNAUTHORIZED);
	}

}
