package com.example.spring5mvcstoreapp.services;

public class UnsatisfactoryItemAmountInCartException extends RuntimeException {

    public UnsatisfactoryItemAmountInCartException(String message) {
        super(message);
    }
}