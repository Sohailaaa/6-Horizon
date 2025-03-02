package com.example.repository;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class CartRepository extends MainRepository<Cart> {
    @Override
    protected String getDataPath() {
        return "src/main/java/com/example/data/carts.json";

    }

    @Override
    protected Class<Cart[]> getArrayType() {
        return Cart[].class;
    }

    public CartRepository() {
    }

    public Cart addCart(Cart cart) {
       save(cart);
       return cart;
    }
//ganna
    public ArrayList<Cart> getCarts() {
        return findAll();
    }
//john
    public Cart getCartById(UUID cartId) {
        return null;
    }
//john
    public void addProductToCart(UUID cartId, Product product) {

    }
    public Cart getCartByUserId(UUID userId) {
        List<Cart> carts = getCarts();
        if (carts == null || carts.isEmpty()) {
            return null; // Or throw an exception if appropriate
        }

        for (Cart cart : carts) {
            if (cart.getUserId().equals(userId)) {
                return cart;
            }
        }
        return null;
    }


    //john
    public void deleteProductFromCart(UUID cartId, Product product) {

    }
//john
    public void deleteCartById(UUID cartId) {

    }

}