package com.example.spring5mvcstoreapp.mapper;

import com.example.spring5mvcstoreapp.api.v1.model.CartDTO;
import com.example.spring5mvcstoreapp.api.v1.model.ItemDTO;
import com.example.spring5mvcstoreapp.domain.Cart;
import com.example.spring5mvcstoreapp.domain.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        CartMapperImpl.class
})
class CartMapperTest {
    @Autowired
    private CartMapper cartMapper;


    @Test
    void cartToCartDTO() {
        Item item = Item.builder().id(1L).productName("chocolate").productId(1L).quantity(2).build();
        ItemDTO itemDTO = ItemDTO.builder().productId(1L).productName("chocolate").quantity(2).build();
        Cart cart = Cart.builder().id(1L).subtotal(10.0).build();
        cart.addItem(item);
        CartDTO cartDTO = CartDTO.builder().items(List.of(itemDTO)).subtotal(10.0).build();
        assertEquals(cartMapper.cartToCartDTO(cart), cartDTO);
    }
}