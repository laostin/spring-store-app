package com.example.spring5mvcstoreapp.controllers;

import com.example.spring5mvcstoreapp.api.v1.model.CartDTO;
import com.example.spring5mvcstoreapp.api.v1.model.ItemDTO;
import com.example.spring5mvcstoreapp.domain.Cart;
import com.example.spring5mvcstoreapp.domain.Item;
import com.example.spring5mvcstoreapp.domain.Product;
import com.example.spring5mvcstoreapp.mapper.CartMapper;
import com.example.spring5mvcstoreapp.mapper.ItemMapper;
import com.example.spring5mvcstoreapp.repositories.CartRepository;
import com.example.spring5mvcstoreapp.repositories.ProductRepository;
import com.example.spring5mvcstoreapp.services.CartService;
import com.example.spring5mvcstoreapp.services.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.List;

import static com.example.spring5mvcstoreapp.controllers.AbstractRestControllerTest.asJsonString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartMapper cartMapper;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductRepository productRepository;
    CartService cartService;
    CartController cartController;
    MockMvc mockMvc;
    private final String CART_CONTROLLER_URL = "/api/v1/cart/";

    @BeforeEach
    public void setUp() {
        cartService = new CartServiceImpl(itemMapper, cartMapper, cartRepository, productRepository);
        cartController = new CartController(cartService);
        mockMvc = MockMvcBuilders.standaloneSetup(cartController)
                .setControllerAdvice(new RestResponseEntityExceptionHandler())
                .build();
    }

    @Test
    void addItemToCartTest() throws Exception {
        ItemDTO itemDTOToBeAdded = ItemDTO.builder().productId(1L).productName("milk").quantity(5).build();
        Item item = Item.builder().id(1L).productId(1L).productName("milk").quantity(5).build();
        CartDTO cartDTO = CartDTO.builder().subtotal(10.0).items(List.of(itemDTOToBeAdded)).build();
        Cart cart = Cart.builder().subtotal(0.0).build();
        Product product = Product.builder().id(1L).title("milk").available(10).price(2.0).build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(itemMapper.itemDTOToItem(itemDTOToBeAdded)).thenReturn(item);
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(cartMapper.cartToCartDTO(cart)).thenReturn(cartDTO);

        mockMvc.perform(post(CART_CONTROLLER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemDTOToBeAdded)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.subtotal", equalTo(10.0)));
    }

    @Test
    void addItemToCartIfItemAlreadyInCartTest() throws Exception {
        ItemDTO itemDTO = ItemDTO.builder().productId(1L).productName("milk").quantity(5).build();
        Item item = Item.builder().id(1L).productId(1L).productName("milk").quantity(5).build();
        Cart cart = Cart.builder().subtotal(10.0).build();
        cart.addItem(item);

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(itemMapper.itemDTOToItem(itemDTO)).thenReturn(item);

        mockMvc.perform(post(CART_CONTROLLER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void addItemToCartIfItemNotInStoreTest() throws Exception {
        ItemDTO itemDTO = ItemDTO.builder().productId(1L).productName("milk").quantity(5).build();
        Item item = Item.builder().id(1L).productId(1L).productName("milk").quantity(5).build();
        Cart cart = Cart.builder().subtotal(0.0).build();
        Product product = Product.builder().id(2L).title("sugar").available(20).price(2.0).build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(itemMapper.itemDTOToItem(itemDTO)).thenReturn(item);
        when(productRepository.findAll()).thenReturn(List.of(product));

        mockMvc.perform(post(CART_CONTROLLER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void displayCartContentTest() throws Exception {
        Item item = Item.builder().id(1L).productId(1L).productName("milk").quantity(5).build();
        Cart cart = Cart.builder().subtotal(10.0).build();
        cart.addItem(item);

        when(cartRepository.findAll()).thenReturn(List.of(cart));

        mockMvc.perform(get(CART_CONTROLLER_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void displayCartContentIfCartIsEmptyTest() throws Exception {
        Cart cart = Cart.builder().build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));

        mockMvc.perform(get(CART_CONTROLLER_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeItemFromCartTest() throws Exception {
        Item item1 = Item.builder().id(1L).productId(1L).productName("milk").quantity(5).build();
        Item item2 = Item.builder().id(2L).productId(2L).productName("water").quantity(1).build();
        Cart cart = Cart.builder().subtotal(11.0).build();
        cart.addItem(item1);
        cart.addItem(item2);
        Product product = Product.builder().id(1L).title("milk").available(10).price(2.0).build();
        ItemDTO itemDTO2 = ItemDTO.builder().productId(2L).productName("water").quantity(1).build();
        CartDTO cartDTO = CartDTO.builder().items(List.of(itemDTO2)).subtotal(1.0).build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(cartMapper.cartToCartDTO(cart)).thenReturn(cartDTO);

        mockMvc.perform(delete(CART_CONTROLLER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.subtotal", equalTo(1.0)));
    }

    @Test
    void removeItemFromCartIfCartIsEmptyTest() throws Exception {
        Cart cart = Cart.builder().build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));

        mockMvc.perform(delete(CART_CONTROLLER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeItemFromCartIfInputNotNumberTest() throws Exception {
        Cart cart = Cart.builder().build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));

        mockMvc.perform(delete(CART_CONTROLLER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString("one")))
                .andExpect(status().isNotFound());
    }

    @Test
    void modifyCartItemTest() throws Exception {
        ItemDTO itemDTOToBeModify = ItemDTO.builder().productId(1L).productName("milk").quantity(2).build();
        Item itemToBeModify = Item.builder().id(1L).productId(1L).productName("milk").quantity(2).build();
        Item item = Item.builder().id(1L).productId(1L).productName("milk").quantity(5).build();
        CartDTO cartDTO = CartDTO.builder().subtotal(4.0).items(List.of(itemDTOToBeModify)).build();
        Cart cart = Cart.builder().subtotal(10.0).build();
        cart.addItem(item);
        Product product = Product.builder().id(1L).title("milk").available(10).price(2.0).build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(itemMapper.itemDTOToItem(itemDTOToBeModify)).thenReturn(itemToBeModify);
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(cartMapper.cartToCartDTO(cart)).thenReturn(cartDTO);

        mockMvc.perform(put(CART_CONTROLLER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemDTOToBeModify)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.subtotal", equalTo(4.0)));
    }

    @Test
    void modifyCartItemIfCartIsEmptyTest() throws Exception {
        Cart cart = Cart.builder().build();
        ItemDTO itemDTOToBeModify = ItemDTO.builder().productId(1L).productName("milk").quantity(2).build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));

        mockMvc.perform(put(CART_CONTROLLER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemDTOToBeModify)))
                .andExpect(status().isNotFound());
    }

    @Test
    void modifyCartItemIfItemNotInCartTest() throws Exception {
        ItemDTO itemDTOToBeModify = ItemDTO.builder().productId(2L).productName("sugar").quantity(1).build();
        Item itemToBeModify = Item.builder().id(2L).productId(2L).productName("sugar").quantity(1).build();
        Item item = Item.builder().id(1L).productId(1L).productName("milk").quantity(5).build();
        Cart cart = Cart.builder().subtotal(10.0).build();
        cart.addItem(item);

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(itemMapper.itemDTOToItem(itemDTOToBeModify)).thenReturn(itemToBeModify);

        mockMvc.perform(put(CART_CONTROLLER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemDTOToBeModify)))
                .andExpect(status().isNotFound());
    }

    @Test
    void modifyCartItemIfItemInStoreNotEnoughTest() throws Exception {
        ItemDTO itemDTOToBeModify = ItemDTO.builder().productId(1L).productName("milk").quantity(5).build();
        Item itemToBeModify = Item.builder().id(1L).productId(1L).productName("milk").quantity(5).build();
        Item item = Item.builder().id(1L).productId(1L).productName("milk").quantity(2).build();
        Cart cart = Cart.builder().subtotal(10.0).build();
        cart.addItem(item);
        Product product = Product.builder().id(1L).title("milk").available(1).price(2.0).build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(itemMapper.itemDTOToItem(itemDTOToBeModify)).thenReturn(itemToBeModify);
        when(productRepository.findAll()).thenReturn(List.of(product));

        mockMvc.perform(put(CART_CONTROLLER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemDTOToBeModify)))
                .andExpect(status().isNotFound());
    }

    @Test
    void checkoutTest() throws Exception {
        Item item = Item.builder().id(1L).productId(1L).productName("milk").quantity(2).build();
        Cart cart = Cart.builder().subtotal(4.0).build();
        cart.addItem(item);
        Product product = Product.builder().id(1L).title("milk").available(10).price(2.0).build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(productRepository.findAll()).thenReturn(List.of(product));

        mockMvc.perform(post(CART_CONTROLLER_URL + "/checkout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void checkoutIfCartIsEmptyTest() throws Exception {
        Cart cart = Cart.builder().build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));

        mockMvc.perform(post(CART_CONTROLLER_URL + "/checkout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void checkoutIfItemAmountIsNotEnoughTest() throws Exception {
        Item item = Item.builder().id(1L).productId(1L).productName("milk").quantity(2).build();
        Cart cart = Cart.builder().subtotal(4.0).build();
        cart.addItem(item);
        Product product = Product.builder().id(1L).title("milk").available(-1).price(2.0).build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(productRepository.findAll()).thenReturn(List.of(product));

        mockMvc.perform(post(CART_CONTROLLER_URL + "/checkout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void checkoutIfPriceIsIncorrectTest() {
        Item item = Item.builder().id(1L).productId(1L).productName("milk").quantity(2).build();
        Cart cart = Cart.builder().subtotal(10.0).build();
        cart.addItem(item);
        Product product = Product.builder().id(1L).title("milk").available(10).price(2.0).build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(productRepository.findAll()).thenReturn(List.of(product));

        NestedServletException exception = assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(post(CART_CONTROLLER_URL + "/checkout")
                    .contentType(MediaType.APPLICATION_JSON));
        });

        Throwable nestedException = exception.getRootCause();
        assertTrue(nestedException instanceof RuntimeException);
        assertEquals("Price in cart is incorrect", nestedException.getMessage());
    }

    @Test
    void cancelOrderTest() throws Exception {
        Item item = Item.builder().id(1L).productId(1L).productName("milk").quantity(2).build();
        Cart cart = Cart.builder().subtotal(4.0).build();
        cart.addItem(item);
        Product product = Product.builder().id(1L).title("milk").available(10).price(2.0).build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(productRepository.findAll()).thenReturn(List.of(product));

        mockMvc.perform(post(CART_CONTROLLER_URL + "/cancelOrder")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void cancelOrderIfCartIsEmptyTest() throws Exception {
        Cart cart = Cart.builder().build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));

        mockMvc.perform(post(CART_CONTROLLER_URL + "/cancelOrder")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}