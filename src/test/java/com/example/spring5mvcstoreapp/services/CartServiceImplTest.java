package com.example.spring5mvcstoreapp.services;

import com.example.spring5mvcstoreapp.api.v1.model.CartDTO;
import com.example.spring5mvcstoreapp.api.v1.model.ItemDTO;
import com.example.spring5mvcstoreapp.domain.Cart;
import com.example.spring5mvcstoreapp.domain.Item;
import com.example.spring5mvcstoreapp.domain.Product;
import com.example.spring5mvcstoreapp.mapper.CartMapper;
import com.example.spring5mvcstoreapp.mapper.ItemMapper;
import com.example.spring5mvcstoreapp.repositories.CartRepository;
import com.example.spring5mvcstoreapp.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {
    @Mock
    private CartMapper cartMapper;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductRepository productRepository;
    private CartService cartService;

    @BeforeEach
    public void setUp() {
        cartService = new CartServiceImpl(itemMapper, cartMapper, cartRepository, productRepository);
    }

    @Test
    void addItemToCartTest() {
        Item item = Item.builder().id(1L).productName("chocolate").productId(1L).quantity(2).build();
        ItemDTO itemDTO = ItemDTO.builder().productId(1L).productName("chocolate").quantity(2).build();
        CartDTO expected = CartDTO.builder().items(List.of(itemDTO)).subtotal(4.0).build();

        when(itemMapper.itemDTOToItem(any(ItemDTO.class))).thenReturn(item);
        when(productRepository.findAll()).thenReturn(List.of(Product.builder().id(1L).available(10).price(2.0).build()));
        when(cartMapper.cartToCartDTO(any(Cart.class))).thenReturn(expected);
        CartDTO actual = cartService.addItemToCart(itemDTO);

        assertEquals(expected, actual);
    }

    @Test
    void addItemToCartIfAlreadyInCartTest() {
        Item item = Item.builder().id(1L).productName("chocolate").productId(1L).quantity(20).build();
        ItemDTO itemDTO = ItemDTO.builder().productId(1L).productName("chocolate").quantity(20).build();
        Cart cart = Cart.builder().id(1L).subtotal(10.0).build();
        cart.addItem(item);

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(itemMapper.itemDTOToItem(itemDTO)).thenReturn(item);

        assertThrows(ItemAlreadyInCartException.class, () -> cartService.addItemToCart(itemDTO));
    }

    @Test
    void addItemToCartIfNotInStoreTest() {
        Item item = Item.builder().id(1L).productName("chocolate").productId(1L).quantity(20).build();
        ItemDTO itemDTO = ItemDTO.builder().productId(1L).productName("chocolate").quantity(20).build();
        Product product = Product.builder().id(2L).title("ice cream").available(10).build();

        when(productRepository.findAll()).thenReturn(List.of(product));
        when(itemMapper.itemDTOToItem(itemDTO)).thenReturn(item);

        assertThrows(ProductNotFoundException.class, () -> cartService.addItemToCart(itemDTO));
    }

    @Test
    void addItemToCartIfAmountMoreThanInStoreTest() {
        Item item = Item.builder().id(1L).productName("chocolate").productId(1L).quantity(20).build();
        ItemDTO itemDTO = ItemDTO.builder().productId(1L).productName("chocolate").quantity(20).build();

        when(itemMapper.itemDTOToItem(any(ItemDTO.class))).thenReturn(item);
        when(productRepository.findAll()).thenReturn(List.of(Product.builder().id(1L).available(10).price(2.0).build()));

        assertThrows(UnsatisfactoryItemAmountInCartException.class, () -> cartService.addItemToCart(itemDTO));
    }

    @Test
    void displayCartContentTest() {
        Item item = Item.builder().id(1L).productName("chocolate").productId(1L).quantity(20).build();
        Cart cart = Cart.builder().id(1L).subtotal(10.0).build();
        cart.addItem(item);

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        String result = "Products amount in cart - 1: \n" +
                "1. chocolate - 20pc\n" +
                "----------------\n" +
                "Subtotal: 10.0";
        assertEquals(cartService.displayCartContent(), result);
    }


    @Test
    void displayCartContentIfCartIsEmptyTest() {
        assertThrows(ProductNotFoundException.class, () -> cartService.displayCartContent());
    }

    @Test
    void removeItemFromCartTest() {
        Item item = Item.builder().id(1L).productName("chocolate").productId(1L).quantity(2).build();
        Cart cart = Cart.builder().id(1L).subtotal(10.0).build();
        cart.addItem(item);
        Product product = Product.builder().id(1L).title("chocolate").price(5.0).available(30).build();
        CartDTO cartDTO = CartDTO.builder().build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(cartMapper.cartToCartDTO(cart)).thenReturn(cartDTO);

        assertEquals(cartService.removeItemFromCart("1"), cartDTO);
    }

    @Test
    void removeItemFromCartIfIncorrectInputTest() {
        Item item = Item.builder().id(1L).productName("chocolate").productId(1L).quantity(2).build();
        Cart cart = Cart.builder().id(1L).subtotal(10.0).build();
        cart.addItem(item);

        when(cartRepository.findAll()).thenReturn(List.of(cart));

        assertThrows(NumberFormatException.class, () -> cartService.removeItemFromCart("one"));
    }

    @Test
    void removeItemFromCartIfNotInCartTest() {
        Item item = Item.builder().id(1L).productName("chocolate").productId(1L).quantity(2).build();
        Cart cart = Cart.builder().id(1L).subtotal(10.0).build();
        cart.addItem(item);

        when(cartRepository.findAll()).thenReturn(List.of(cart));

        assertThrows(ProductNotFoundException.class, () -> cartService.removeItemFromCart("2"));
    }

    @Test
    void decreaseAmountOfItemInCartTest() {
        Item item = Item.builder().id(1L).productName("chocolate").productId(1L).quantity(5).build();
        Cart cart = Cart.builder().id(1L).subtotal(25.0).build();
        cart.addItem(item);
        Item itemToBeModify = Item.builder().id(1L).productName("chocolate").productId(1L).quantity(2).build();
        ItemDTO itemDTO = ItemDTO.builder().productId(1L).productName("chocolate").quantity(2).build();
        Product product = Product.builder().id(1L).title("chocolate").price(5.0).available(30).build();
        CartDTO expected = CartDTO.builder().items(List.of(itemDTO)).subtotal(10.0).build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(itemMapper.itemDTOToItem(itemDTO)).thenReturn(itemToBeModify);
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(cartMapper.cartToCartDTO(cart)).thenReturn(expected);

        assertEquals(cartService.modifyCartItem(itemDTO), expected);
    }

    @Test
    void increaseAmountOfItemInCartTest() {
        Item item = Item.builder().id(1L).productName("chocolate").productId(1L).quantity(1).build();
        Cart cart = Cart.builder().id(1L).subtotal(5.0).build();
        cart.addItem(item);
        Item itemToBeModify = Item.builder().id(1L).productName("chocolate").productId(1L).quantity(3).build();
        ItemDTO itemDTO = ItemDTO.builder().productId(1L).productName("chocolate").quantity(3).build();
        Product product = Product.builder().id(1L).title("chocolate").price(5.0).available(30).build();
        CartDTO expected = CartDTO.builder().items(List.of(itemDTO)).subtotal(15.0).build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(itemMapper.itemDTOToItem(itemDTO)).thenReturn(itemToBeModify);
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(cartMapper.cartToCartDTO(cart)).thenReturn(expected);

        assertEquals(cartService.modifyCartItem(itemDTO), expected);
    }

    @Test
    void modifyItemInCartIfCartIsEmptyTest() {
        Cart cart = Cart.builder().build();
        ItemDTO itemDTO = ItemDTO.builder().productId(1L).productName("chocolate").quantity(3).build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));

        assertThrows(ProductNotFoundException.class, () -> cartService.modifyCartItem(itemDTO));
    }

    @Test
    void modifyItemInCartIfItemIsNotInCartTest() {
        Item item = Item.builder().id(1L).productName("chocolate").productId(1L).quantity(1).build();
        Cart cart = Cart.builder().id(1L).subtotal(5.0).build();
        cart.addItem(item);
        Item itemToBeModify = Item.builder().id(2L).productName("candy").productId(2L).quantity(3).build();
        ItemDTO itemDTO = ItemDTO.builder().productId(2L).productName("candy").quantity(3).build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(itemMapper.itemDTOToItem(itemDTO)).thenReturn(itemToBeModify);

        assertThrows(ProductNotFoundException.class, () -> cartService.modifyCartItem(itemDTO));
    }

    @Test
    void checkoutTest() {
        Item item = Item.builder().id(1L).productName("chocolate").productId(1L).quantity(1).build();
        Cart cart = Cart.builder().id(1L).subtotal(5.0).build();
        cart.addItem(item);
        Product product = Product.builder().id(1L).title("chocolate").price(5.0).available(30).build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(productRepository.findAll()).thenReturn(List.of(product));

        assertEquals(cartService.checkout(), "Order has been placed! Total price is: 5.0");
    }

    @Test
    void checkoutIfCartIsEmpty() {
        Cart cart = Cart.builder().build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));

        assertThrows(ProductNotFoundException.class, () -> cartService.checkout());
    }

    @Test
    void checkoutIfPriceIsNotAcceptableTest() {
        Item item = Item.builder().id(1L).productName("chocolate").productId(1L).quantity(5).build();
        Cart cart = Cart.builder().id(1L).subtotal(5.0).build();
        cart.addItem(item);
        Product product = Product.builder().id(1L).title("chocolate").price(5.0).available(1).build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(productRepository.findAll()).thenReturn(List.of(product));

        assertThrows(RuntimeException.class, () -> cartService.checkout());
    }

    @Test
    void checkoutIfAmountInStoreIsNotAcceptableTest() {
        Item item = Item.builder().id(1L).productName("chocolate").productId(1L).quantity(1).build();
        Cart cart = Cart.builder().id(1L).subtotal(5.0).build();
        cart.addItem(item);
        Product product = Product.builder().id(1L).title("chocolate").price(5.0).available(-1).build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(productRepository.findAll()).thenReturn(List.of(product));

        assertThrows(RuntimeException.class, () -> cartService.checkout());
    }

    @Test
    void cancelOrderTest() {
        Item item = Item.builder().id(1L).productName("chocolate").productId(1L).quantity(1).build();
        Cart cart = Cart.builder().id(1L).subtotal(5.0).build();
        cart.addItem(item);
        Product product = Product.builder().id(1L).title("chocolate").price(5.0).available(5).build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(productRepository.findAll()).thenReturn(List.of(product));

        assertEquals(cartService.cancelOrder(), "Cart is empty");
    }

    @Test
    void cancelOrderIfCartIsEmptyTest() {
        Cart cart = Cart.builder().build();

        when(cartRepository.findAll()).thenReturn(List.of(cart));

        assertThrows(ProductNotFoundException.class, () -> cartService.cancelOrder());
    }
}