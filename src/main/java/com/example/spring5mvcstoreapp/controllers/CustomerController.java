package com.example.spring5mvcstoreapp.controllers;

import com.example.spring5mvcstoreapp.api.v1.model.CustomerDTO;
import com.example.spring5mvcstoreapp.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String registerNewCustomer(@RequestBody CustomerDTO customerDTO) {
        return customerService.registerNewCustomer(customerDTO);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public String logInIntoSystem(@RequestBody CustomerDTO customerDTO) {
        return customerService.logInIntoSystem(customerDTO);
    }

    @PutMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public String updateCustomerPassword(@PathVariable String email, @RequestBody String password) {
        return customerService.updatePasswordForCustomer(email, password);
    }
}
