package com.example.spring5mvcstoreapp.services;

import com.example.spring5mvcstoreapp.api.v1.model.CustomerDTO;
import com.example.spring5mvcstoreapp.domain.Customer;
import com.example.spring5mvcstoreapp.mapper.CustomerMapper;
import com.example.spring5mvcstoreapp.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerMapper customerMapper;

    private CustomerService customerService;
    private final String EMAIL = "111@email.com";
    private final String UNREGISTERED_USER_EMAIL = "222@email.com";
    private final String PASSWORD = "111";
    private final String UNREGISTERED_USER_PASSWORD = "222";

    @BeforeEach
    public void setUp() {
        customerService = new CustomerServiceImpl(customerRepository, customerMapper);
    }

    @Test
    void registerNewCustomerTest() {
        CustomerDTO newCustomerDTO = CustomerDTO.builder().email(EMAIL).password(PASSWORD).build();
        Customer newCustomer = Customer.builder().id(2L).email(EMAIL).password(PASSWORD).build();

        when(customerMapper.customerDTOToCustomer(any(CustomerDTO.class))).thenReturn(newCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);

        String actual = customerService.registerNewCustomer(newCustomerDTO);
        assertEquals("User " + EMAIL + " registered successfully", actual);
    }

    @Test
    void registerCustomerWithExistingEmailTest() {
        CustomerDTO customerDTO = CustomerDTO.builder().email(EMAIL).password(PASSWORD).build();
        Customer customer = Customer.builder().id(2L).email(EMAIL).password(PASSWORD).build();

        when(customerRepository.findAll()).thenReturn(List.of(customer));

        assertThrows(CustomerExistException.class, () -> customerService.registerNewCustomer(customerDTO));
    }

    @Test
    void registeredCustomerLogInIntoSystemTest() {
        String ps = BCrypt.hashpw(PASSWORD, BCrypt.gensalt());
        CustomerDTO customerDTO = CustomerDTO.builder().email(EMAIL).password(PASSWORD).build();
        Customer customer = Customer.builder().id(2L).email(EMAIL).sessionId(7).password(ps).build();

        when(customerRepository.findAll()).thenReturn(List.of(customer));

        assertEquals(customerService.logInIntoSystem(customerDTO), "Session id is: " + customer.getSessionId());
    }

    @Test
    void unregisteredCustomerLogInIntoSystemTest() {
        CustomerDTO unregisteredCustomerDTO = CustomerDTO.builder().email(UNREGISTERED_USER_EMAIL).password(UNREGISTERED_USER_PASSWORD).build();
        Customer customer = Customer.builder().id(2L).email(EMAIL).password(PASSWORD).build();

        when(customerRepository.findAll()).thenReturn(List.of(customer));

        assertThrows(CustomerExistException.class, () -> customerService.logInIntoSystem(unregisteredCustomerDTO));
    }


    @Test
    void updatePasswordForCustomerTest() {
        String newPassword = "333";
        String ps = BCrypt.hashpw(PASSWORD, BCrypt.gensalt());
        Customer customer = Customer.builder().id(1L).email(EMAIL).sessionId(1).password(ps).build();

        when(customerRepository.findAll()).thenReturn(List.of(customer));

        assertEquals(customerService.updatePasswordForCustomer(EMAIL, newPassword), "Password updated successfully");
    }

    @Test
    void updatePasswordForUnregisteredCustomerTest() {
        String newPassword = "333";
        Customer customer = Customer.builder().id(1L).email(EMAIL).password(PASSWORD).build();

        when(customerRepository.findAll()).thenReturn(List.of(customer));

        assertThrows(CustomerExistException.class, () -> customerService.updatePasswordForCustomer(UNREGISTERED_USER_EMAIL, newPassword));
    }
}