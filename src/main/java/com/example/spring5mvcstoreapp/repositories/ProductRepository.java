package com.example.spring5mvcstoreapp.repositories;

import com.example.spring5mvcstoreapp.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
