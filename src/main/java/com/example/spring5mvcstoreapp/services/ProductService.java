package com.example.spring5mvcstoreapp.services;

import com.example.spring5mvcstoreapp.api.v1.model.ProductListDTO;

public interface ProductService {
    ProductListDTO getAllProducts();
}
