package com.example.spring5mvcstoreapp.services;

import com.example.spring5mvcstoreapp.api.v1.model.CartDTO;
import com.example.spring5mvcstoreapp.api.v1.model.ItemDTO;

public interface CartService {
    CartDTO addItemToCart(ItemDTO item);

    String displayCartContent();

    CartDTO removeItemFromCart(String productId);

    CartDTO modifyCartItem(ItemDTO itemDTO);

    String checkout();

    String cancelOrder();

}
