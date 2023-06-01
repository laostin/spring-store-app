package com.example.spring5mvcstoreapp.services;

import com.example.spring5mvcstoreapp.api.v1.model.CustomerDTO;
import com.example.spring5mvcstoreapp.domain.Customer;
import com.example.spring5mvcstoreapp.mapper.CustomerMapper;
import com.example.spring5mvcstoreapp.repositories.CustomerRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public String registerNewCustomer(CustomerDTO customerDTO) {
        Customer customer = findCustomerByEmail(customerDTO.getEmail());
        if (customer != null)
            throw new CustomerExistException("Customer with email " + customerDTO.getEmail() + " already exist.");

        Customer newCustomer = customerMapper.customerDTOToCustomer(customerDTO);

        String hashedPassword = BCrypt.hashpw(newCustomer.getPassword(), BCrypt.gensalt());
        newCustomer.setPassword(hashedPassword);
        saveCustomer(newCustomer);
        return "User " + newCustomer.getEmail() + " registered successfully";
    }

    @Override
    public String logInIntoSystem(CustomerDTO customerDTO) {
        int sessionId = generateSessionId();

        Customer customer = findCustomerByEmail(customerDTO.getEmail());

        if (customer == null || !BCrypt.checkpw(customerDTO.getPassword(), customer.getPassword())) {
            throw new CustomerExistException("Invalid email or password");
        }

        customer.setSessionId(sessionId);
        saveCustomer(customer);
        return "Session id is: " + sessionId;
    }

    @Override
    public String updatePasswordForCustomer(String email, String newPassword) {
        Customer customer = findCustomerByEmail(email);

        if (customer == null) throw new CustomerExistException("Customer with email " + email + " is not registered");

        if (customer.getSessionId() == null) throw new UnauthorizedException("Please log in");
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        customer.setPassword(hashedPassword);
        customerRepository.save(customer);
        return "Password updated successfully";
    }

    private int generateSessionId() {
        Random random = new Random();
        return random.nextInt(10) + 1;
    }

    private Customer findCustomerByEmail(String email) {
        return customerRepository.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    private void saveCustomer(Customer customer) {
        customerRepository.save(customer);
    }
}
