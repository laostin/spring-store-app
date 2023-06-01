package com.example.spring5mvcstoreapp.mapper;

import com.example.spring5mvcstoreapp.api.v1.model.CartDTO;
import com.example.spring5mvcstoreapp.domain.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartDTO cartToCartDTO(Cart cart);
}
