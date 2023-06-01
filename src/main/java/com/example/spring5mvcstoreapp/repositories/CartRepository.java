package com.example.spring5mvcstoreapp.repositories;

import com.example.spring5mvcstoreapp.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
