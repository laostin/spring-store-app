package com.example.spring5mvcstoreapp.bootstrap;

import com.example.spring5mvcstoreapp.domain.Product;
import com.example.spring5mvcstoreapp.repositories.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap implements CommandLineRunner {
    private final ProductRepository productRepository;

    public Bootstrap(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        loadProducts();
    }

    private void loadProducts() {
        Product bread = Product.builder().title("White bread").available(10).price(2.0).build();
        Product milk = Product.builder().title("Almond milk").available(3).price(10.0).build();

        productRepository.save(bread);
        productRepository.save(milk);
        System.out.println("Product loaded: " + productRepository.count());
    }
}
