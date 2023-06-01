package com.example.spring5mvcstoreapp.services;

import com.example.spring5mvcstoreapp.api.v1.model.ProductListDTO;
import com.example.spring5mvcstoreapp.mapper.ProductMapper;
import com.example.spring5mvcstoreapp.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductMapper productMapper, ProductRepository productRepository) {
        this.productMapper = productMapper;
        this.productRepository = productRepository;
    }

    @Override
    public ProductListDTO getAllProducts() {
        ProductListDTO productListDTO = new ProductListDTO();
        productListDTO.setProducts(productRepository.findAll().stream()
                .map(productMapper::productToProductDTO)
                .collect(Collectors.toList()));
        return productListDTO;
    }
}
