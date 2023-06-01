package com.example.spring5mvcstoreapp.services;

public class ItemAlreadyInCartException extends RuntimeException {
    public ItemAlreadyInCartException(String message) {
        super(message);
    }
}
