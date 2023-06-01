package com.example.spring5mvcstoreapp.services;

import com.example.spring5mvcstoreapp.api.v1.model.ProductDTO;
import com.example.spring5mvcstoreapp.api.v1.model.ProductListDTO;
import com.example.spring5mvcstoreapp.domain.Product;
import com.example.spring5mvcstoreapp.mapper.ProductMapper;
import com.example.spring5mvcstoreapp.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    private ProductService productService;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    public void setUp() {
        productService = new ProductServiceImpl(productMapper, productRepository);
    }

    @Test
    void getAllProductsTest() {

        ProductDTO bread = ProductDTO.builder().title("White bread").available(10).price(2.0).build();
        ProductDTO milk = ProductDTO.builder().title("Almond milk").available(3).price(10.0).build();
        Product bread1 = Product.builder().title("White bread").available(10).price(2.0).build();
        Product milk1 = Product.builder().title("Almond milk").available(3).price(10.0).build();
        ProductListDTO productListDTO = new ProductListDTO();
        productListDTO.setProducts(List.of(bread, milk));

        when(productMapper.productToProductDTO(bread1)).thenReturn(bread);
        when(productMapper.productToProductDTO(milk1)).thenReturn(milk);
        when(productRepository.findAll()).thenReturn(List.of(bread1, milk1));

        assertEquals(productService.getAllProducts(), productListDTO);
    }
}