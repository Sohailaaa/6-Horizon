package com.example.service;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class CartService extends MainService<Cart> {

    private final CartRepository cartRepository;

    @Autowired
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Cart addCart(Cart cart) {
        return cartRepository.addCart(cart);
    }

    public ArrayList<Cart> getCarts() {
        return null;
    }

    public Cart getCartById(UUID cartId) {
        return null;
    }

    public Cart getCartByUserId(UUID userId) {
        return null;
    }

    public void addProductToCart(UUID cartId, Product product) {
    }

    public void deleteProductFromCart(UUID cartId, Product product) {
    }

    public void deleteCartById(UUID cartId) {
    }
}