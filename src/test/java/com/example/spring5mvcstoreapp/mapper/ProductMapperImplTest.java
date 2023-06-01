package com.example.spring5mvcstoreapp.mapper;

import com.example.spring5mvcstoreapp.api.v1.model.ProductDTO;
import com.example.spring5mvcstoreapp.domain.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        ProductMapperImpl.class
})
class ProductMapperImplTest {

    @Autowired
    private ProductMapper mapper;


    @Test
    void productToProductDTO() {
        Product product = Product.builder().id(1L).title("Sparkling water").price(1.0).available(20).build();
        ProductDTO expectedProduct = ProductDTO.builder().id(1L).title("Sparkling water").price(1.0).available(20).build();

        assertEquals(expectedProduct, mapper.productToProductDTO(product));
    }
}