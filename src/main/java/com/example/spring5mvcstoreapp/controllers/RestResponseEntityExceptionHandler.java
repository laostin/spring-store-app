package com.example.spring5mvcstoreapp.controllers;

import com.example.spring5mvcstoreapp.services.CustomerExistException;
import com.example.spring5mvcstoreapp.services.ItemAlreadyInCartException;
import com.example.spring5mvcstoreapp.services.ProductNotFoundException;
import com.example.spring5mvcstoreapp.services.UnauthorizedException;
import com.example.spring5mvcstoreapp.services.UnsatisfactoryItemAmountInCartException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({ProductNotFoundException.class, UnsatisfactoryItemAmountInCartException.class, NumberFormatException.class})
    public ResponseEntity<Object> handleProductNotFoundException(Exception exception, WebRequest webRequest) {
        return new ResponseEntity<Object>(exception.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({CustomerExistException.class, ItemAlreadyInCartException.class})
    public ResponseEntity<Object> handleAlreadyExistException(Exception exception, WebRequest webRequest) {
        return new ResponseEntity<Object>(exception.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<Object> handleUnauthorizedException(Exception exception, WebRequest webRequest) {
        return new ResponseEntity<Object>(exception.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT);
    }
}

