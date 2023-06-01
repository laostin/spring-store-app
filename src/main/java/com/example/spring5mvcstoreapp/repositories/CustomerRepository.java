package com.example.spring5mvcstoreapp.repositories;

import com.example.spring5mvcstoreapp.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
