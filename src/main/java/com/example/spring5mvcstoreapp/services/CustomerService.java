package com.example.spring5mvcstoreapp.services;

import com.example.spring5mvcstoreapp.api.v1.model.CustomerDTO;

public interface CustomerService {
    String registerNewCustomer(CustomerDTO customerDTO);

    String logInIntoSystem(CustomerDTO customerDTO);

    String updatePasswordForCustomer(String email, String password);
}
