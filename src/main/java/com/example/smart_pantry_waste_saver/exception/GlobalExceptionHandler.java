package com.example.smart_pantry_waste_saver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleConflict(UserAlreadyExistsException ex) {
    	
    	return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    
    
    
    
}