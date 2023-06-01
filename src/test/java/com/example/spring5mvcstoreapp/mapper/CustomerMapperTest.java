package com.example.spring5mvcstoreapp.mapper;

import com.example.spring5mvcstoreapp.api.v1.model.CustomerDTO;
import com.example.spring5mvcstoreapp.domain.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        CustomerMapperImpl.class
})
class CustomerMapperTest {

    @Autowired
    private CustomerMapper customerMapper;

    @Test
    void customerToCustomerDTO() {
        Customer customer = Customer.builder().email("111@email.com").password("123").build();

        CustomerDTO customerDTO = customerMapper.customerToCustomerDTO(customer);

        assertEquals(customer.getEmail(), customerDTO.getEmail());
        assertEquals(customer.getPassword(), customerDTO.getPassword());
    }

    @Test
    void customerDTOToCustomer() {
        CustomerDTO customerDTO = CustomerDTO.builder().email("123@email.com").password("123123").build();

        Customer customer = customerMapper.customerDTOToCustomer(customerDTO);

        assertEquals(customer.getEmail(), customerDTO.getEmail());
        assertEquals(customer.getPassword(), customerDTO.getPassword());
    }
}