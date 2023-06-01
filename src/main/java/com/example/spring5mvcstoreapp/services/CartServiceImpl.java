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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CartServiceImpl implements CartService {
    private final ItemMapper itemMapper;
    private final CartMapper cartMapper;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final Cart defaultCart = Cart.builder().id(1L).subtotal(0.0).build();

    public CartServiceImpl(ItemMapper itemMapper, CartMapper cartMapper, CartRepository cartRepository, ProductRepository productRepository) {
        this.itemMapper = itemMapper;
        this.cartMapper = cartMapper;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Override
    public CartDTO addItemToCart(ItemDTO itemDTO) {
        Cart cart = cartRepository.findAll().stream().findFirst().orElse(defaultCart);
        Item item = itemMapper.itemDTOToItem(itemDTO);
        verifyIfAlreadyInCart(item, cart);

        Product product = getProductInStoreByItemInCart(productRepository.findAll(), item);
        verifyAvailabilityInStore(item.getQuantity(), product);

        item.setId((long) (cart.getItems().size() + 1));
        item.setProductName(product.getTitle());

        cart.addItem(item);
        increaseSubtotalInCart(cart, product, item.getQuantity());
        cartRepository.save(cart);

        decreaseProductAvailabilityInStore(item.getQuantity(), product);
        productRepository.save(product);
        return cartMapper.cartToCartDTO(cart);
    }


    @Override
    public String displayCartContent() {
        Cart cart = cartRepository.findAll().stream().findFirst().orElse(defaultCart);
        verifyListOfItemInCart(cart);

        StringBuilder response = new StringBuilder("Products amount in cart - " + cart.getItems().size() + ": \n");
        int size = cart.getItems().size();
        for (int i = 0; i < size; i++) {
            Item item = cart.getItems().get(i);
            response.append(i + 1).append(". ").append(item.getProductName()).append(" - ").append(item.getQuantity()).append("pc\n");
        }
        response.append("----------------\n");
        response.append("Subtotal: ").append(cart.getSubtotal());
        return response.toString();
    }

    @Override
    public CartDTO removeItemFromCart(String orderOfProduct) {
        Cart cart = cartRepository.findAll().stream().findFirst().orElse(defaultCart);
        verifyListOfItemInCart(cart);

        int orderNumberOfProduct = getProductIdByOrderInCart(cart, orderOfProduct);
        Item itemToBeDelete = cart.getItems().get(orderNumberOfProduct - 1);
        Product product = getProductInStoreByItemInCart(productRepository.findAll(), itemToBeDelete);
        decreaseSubtotalInCart(cart, product, itemToBeDelete.getQuantity());
        cart.removeItem(itemToBeDelete);

        cartRepository.save(cart);
        increaseProductAvailabilityInStore(itemToBeDelete.getQuantity(), product);
        productRepository.save(product);
        return cartMapper.cartToCartDTO(cart);
    }


    @Override
    public CartDTO modifyCartItem(ItemDTO itemDTO) {
        Cart cart = cartRepository.findAll().stream().findFirst().orElse(defaultCart);
        verifyListOfItemInCart(cart);

        Item itemToBeModify = itemMapper.itemDTOToItem(itemDTO);
        Item itemInCart = getPresenceItemInCart(cart, itemToBeModify);

        Product product = getProductInStoreByItemInCart(productRepository.findAll(), itemToBeModify);

        int diff = itemToBeModify.getQuantity() - itemInCart.getQuantity();
        if (diff > 0) {
            verifyAvailabilityInStore(diff, product);
            decreaseProductAvailabilityInStore(diff, product);
            increaseSubtotalInCart(cart, product, diff);
        } else {
            increaseProductAvailabilityInStore(Math.abs(diff), product);
            decreaseSubtotalInCart(cart, product, Math.abs(diff));
        }
        productRepository.save(product);
        cart.getItems()
                .stream()
                .filter(i -> i.getProductId().equals(itemToBeModify.getProductId()))
                .findFirst()
                .ifPresent(item1 -> item1.setQuantity(itemToBeModify.getQuantity()));

        cartRepository.save(cart);

        return cartMapper.cartToCartDTO(cart);
    }

    @Override
    public String checkout() {
        Cart cart = cartRepository.findAll().stream().findFirst().orElse(defaultCart);
        verifyListOfItemInCart(cart);
        List<Product> products = productRepository.findAll();
        double totalPrice = verifyPriceInCart(cart, products);
        verifyAvailableAmountInStore(cart, products);
        cart.emptyCart();
        cartRepository.save(cart);
        return "Order has been placed! Total price is: " + totalPrice;
    }

    @Override
    public String cancelOrder() {
        Cart cart = cartRepository.findAll().stream().findFirst().orElse(defaultCart);
        verifyListOfItemInCart(cart);
        List<Product> products = productRepository.findAll();
        for (Item item : cart.getItems()) {
            Product product = getProductInStoreByItemInCart(products, item);
            product.setAvailable(product.getAvailable() + item.getQuantity());
            productRepository.save(product);
        }
        cart.emptyCart();
        cartRepository.save(cart);
        return "Cart is empty";
    }

    private void verifyIfAlreadyInCart(Item item, Cart cart) {
        boolean isItemInCart = cart.getItems().stream().anyMatch(i -> i.getProductId().equals(item.getProductId()));
        if (isItemInCart) {
            throw new ItemAlreadyInCartException("This product already in cart");
        }
    }

    private Product getProductInStoreByItemInCart(List<Product> products, Item item) {
        return products.stream().filter(p -> p.getId() == item.getProductId().longValue()).findFirst().orElseThrow(() -> new ProductNotFoundException("Product not found in the store."));
    }

    private void verifyAvailabilityInStore(int quantity, Product product) {
        if (product.getAvailable() < quantity)
            throw new UnsatisfactoryItemAmountInCartException("Goods quantity in stock: " + product.getAvailable());

    }

    private void increaseSubtotalInCart(Cart cart, Product product, Integer quantity) {
        double result = cart.getSubtotal() + quantity * product.getPrice();
        cart.setSubtotal(result);
    }

    private void decreaseSubtotalInCart(Cart cart, Product product, Integer quantity) {
        double result = cart.getSubtotal() - quantity * product.getPrice();
        cart.setSubtotal(result);
    }

    private void increaseProductAvailabilityInStore(Integer quantity, Product product) {
        int result = product.getAvailable() + quantity;
        product.setAvailable(result);
    }

    private void decreaseProductAvailabilityInStore(Integer quantity, Product product) {
        int result = product.getAvailable() - quantity;
        product.setAvailable(result);
    }

    private void verifyListOfItemInCart(Cart cart) {
        if (cart.getItems().size() == 0) throw new ProductNotFoundException("The cart is empty");
    }

    private int getProductIdByOrderInCart(Cart cart, String productId) {
        try {
            int id = Integer.parseInt(productId);
            if (id < 1 || id > cart.getItems().size())
                throw new ProductNotFoundException("Please, insert an existing number of item from cart to be deleted");
            return id;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Please, insert a number reflecting the position of the item in the cart to be deleted");
        }
    }

    private Item getPresenceItemInCart(Cart cart, Item item) {
        return cart.getItems().stream().filter(i -> Objects.equals(i.getProductId(), item.getProductId())).findFirst().orElseThrow(() -> new ProductNotFoundException("This item not in cart"));
    }

    private double verifyPriceInCart(Cart cart, List<Product> products) {
        double total = 0.0;
        for (Item item : cart.getItems()) {
            Product product = getProductInStoreByItemInCart(products, item);
            total = total + product.getPrice() * item.getQuantity();
        }
        if (cart.getSubtotal() != total) throw new RuntimeException("Price in cart is incorrect");
        return total;
    }

    private void verifyAvailableAmountInStore(Cart cart, List<Product> products) {
        for (Item item : cart.getItems()) {
            Product product = getProductInStoreByItemInCart(products, item);
            if (product.getAvailable() < 0) {
                throw new UnsatisfactoryItemAmountInCartException(product.getTitle() + " is not available right now");
            }
        }
    }
}
