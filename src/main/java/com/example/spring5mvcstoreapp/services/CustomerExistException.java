package com.example.spring5mvcstoreapp.services;

public class CustomerExistException extends RuntimeException {
    public CustomerExistException(String message) {
        super(message);
    }
}
