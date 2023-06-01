package com.example.spring5mvcstoreapp.controllers;

import com.example.spring5mvcstoreapp.api.v1.model.CustomerDTO;
import com.example.spring5mvcstoreapp.domain.Customer;
import com.example.spring5mvcstoreapp.mapper.CustomerMapper;
import com.example.spring5mvcstoreapp.repositories.CustomerRepository;
import com.example.spring5mvcstoreapp.services.CustomerService;
import com.example.spring5mvcstoreapp.services.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static com.example.spring5mvcstoreapp.controllers.AbstractRestControllerTest.asJsonString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {
    @Mock
    private CustomerMapper customerMapper;
    @Mock
    private CustomerRepository customerRepository;
    CustomerService customerService;
    CustomerController customerController;
    MockMvc mockMvc;
    private final String CUSTOMER_CONTROLLER_URL = "/api/v1/users/";
    private final String EMAIL = "111@email.com";
    private final String UNREGISTERED_USER_EMAIL = "222@email.com";
    private final String PASSWORD = "111";

    @BeforeEach
    public void setUp() {
        customerService = new CustomerServiceImpl(customerRepository, customerMapper);
        customerController = new CustomerController(customerService);
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setControllerAdvice(new RestResponseEntityExceptionHandler())
                .build();
    }

    @Test
    void registerNewCustomerTest() throws Exception {
        CustomerDTO customerDTO = CustomerDTO.builder().email(EMAIL).password(PASSWORD).build();
        Customer customer = Customer.builder().email(EMAIL).password(PASSWORD).build();

        when(customerMapper.customerDTOToCustomer(customerDTO)).thenReturn(customer);

        mockMvc.perform(post(CUSTOMER_CONTROLLER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void registeredUserLogInIntoSystemTest() throws Exception {
        Integer sessionId = 1;
        CustomerDTO customerDTO = CustomerDTO.builder().email(EMAIL).password(PASSWORD).build();
        String ps = BCrypt.hashpw(PASSWORD, BCrypt.gensalt());
        Customer customer = Customer.builder().email(EMAIL).sessionId(sessionId).password(ps).build();


        when(customerRepository.findAll()).thenReturn(List.of(customer));

        mockMvc.perform(post(CUSTOMER_CONTROLLER_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDTO)))
                .andExpect(status().isOk());

    }

    @Test
    void unregisteredUserLogInIntoSystemTest() throws Exception {
        String UNREGISTERED_USER_PASSWORD = "222";
        CustomerDTO customerDTO = CustomerDTO.builder().email(UNREGISTERED_USER_EMAIL).password(UNREGISTERED_USER_PASSWORD).build();
        Customer customer = Customer.builder().email(EMAIL).password(PASSWORD).build();

        when(customerRepository.findAll()).thenReturn(List.of(customer));

        mockMvc.perform(post(CUSTOMER_CONTROLLER_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateCustomerPasswordTest() throws Exception {
        String newPassword = "222";
        String ps = BCrypt.hashpw(PASSWORD, BCrypt.gensalt());
        Customer customer = Customer.builder().sessionId(1).email(EMAIL).password(ps).build();

        when(customerRepository.findAll()).thenReturn(List.of(customer));

        mockMvc.perform(put(CUSTOMER_CONTROLLER_URL + "/" + EMAIL)
                        .content(asJsonString(newPassword)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUnregisteredCustomerPasswordTest() throws Exception {
        String newPassword = "222";
        Customer customer = Customer.builder().email(EMAIL).password(PASSWORD).build();

        when(customerRepository.findAll()).thenReturn(List.of(customer));

        mockMvc.perform(put(CUSTOMER_CONTROLLER_URL + "/" + UNREGISTERED_USER_EMAIL)
                        .content(asJsonString(newPassword)))
                .andExpect(status().isConflict());
    }
}