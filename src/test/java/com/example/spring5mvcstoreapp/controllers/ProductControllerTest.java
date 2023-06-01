package com.example.spring5mvcstoreapp.controllers;

import com.example.spring5mvcstoreapp.api.v1.model.ProductDTO;
import com.example.spring5mvcstoreapp.api.v1.model.ProductListDTO;
import com.example.spring5mvcstoreapp.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private ProductController productController;
    @Mock
    private ProductService productService;

    private final String PRODUCT_CONTROLLER_URL = "/api/v1/products/";

    MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        productController = new ProductController(productService);
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new RestResponseEntityExceptionHandler())
                .build();
    }

    @Test
    void getAllProductsTest() throws Exception {
        ProductDTO bread = ProductDTO.builder().title("White bread").available(10).price(2.0).build();
        ProductDTO milk = ProductDTO.builder().title("Almond milk").available(3).price(10.0).build();
        ProductListDTO productListDTO = new ProductListDTO();
        productListDTO.setProducts(List.of(bread, milk));

        when(productController.getAllProducts()).thenReturn(productListDTO);

        mockMvc.perform(get(PRODUCT_CONTROLLER_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products", hasSize(2)));
    }
}