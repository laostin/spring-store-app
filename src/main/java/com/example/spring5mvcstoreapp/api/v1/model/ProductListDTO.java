package com.example.spring5mvcstoreapp.api.v1.model;

import lombok.Data;

import java.util.List;

@Data
public class ProductListDTO {
    private List<ProductDTO> products;
}
