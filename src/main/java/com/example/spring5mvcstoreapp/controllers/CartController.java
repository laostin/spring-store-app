package com.example.spring5mvcstoreapp.controllers;

import com.example.spring5mvcstoreapp.api.v1.model.CartDTO;
import com.example.spring5mvcstoreapp.api.v1.model.ItemDTO;
import com.example.spring5mvcstoreapp.services.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart/")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public CartDTO addItemToCart(@RequestBody ItemDTO itemDTO) {
        return cartService.addItemToCart(itemDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String displayCartContent() {
        return cartService.displayCartContent();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public CartDTO removeItemFromCart(@RequestBody String productId) {
        return cartService.removeItemFromCart(productId);
    }

    @PutMapping
    public CartDTO modifyCartItem(@RequestBody ItemDTO itemDTO) {
        return cartService.modifyCartItem(itemDTO);
    }

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.OK)
    public String checkout() {
        return cartService.checkout();
    }

    @PostMapping("/cancelOrder")
    @ResponseStatus(HttpStatus.OK)
    public String cancelOrder() {
        return cartService.cancelOrder();
    }
}
