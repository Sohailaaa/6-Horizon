package com.example.repository;

import com.example.model.Cart;
import com.example.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

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

    // Add New Cart
    public Cart addCart(Cart cart) {
        save(cart);
        return cart;
    }

    // Get All Carts
    public ArrayList<Cart> getCarts() {
        ArrayList<Cart> carts = findAll();
        return carts != null ? carts : new ArrayList<>();
    }

    // Get Cart by ID
    public Cart getCartById(UUID cartId) {
        ArrayList<Cart> carts = getCarts();
        for (Cart cart : carts) {
            if (cart.getId().equals(cartId)) {
                return cart;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
    }

    // Get Cart by User ID
    public Cart getCartByUserId(UUID userId) {
        ArrayList<Cart> carts = getCarts();
        for (Cart cart : carts) {
            if (cart.getUserId().equals(userId)) {
                return cart;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
    }

    // Add Product to Cart
    public void addProductToCart(UUID cartId, Product product) {
        ArrayList<Cart> carts = getCarts();
        Cart updated = null;
        for (Cart cart : carts) {
            if (cart.getId().equals(cartId)) {
                updated = cart;
                break;
            }
        }
        if (updated == null) {
            throw new RuntimeException("Cart not found");
        }
        carts.remove(updated);
        updated.getProducts().add(product);
        carts.add(updated);
        saveAll(carts);
    }

    // Delete Product from Cart
    public void deleteProductFromCart(UUID cartId, Product product) {
        Cart cart = getCartById(cartId);
        List<Product> products = cart.getProducts();

        if (products == null || products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart is empty");
        }

        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(product.getId())) {
                products.remove(i);
                save(cart);
                return;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found in cart");
    }

    // Delete Cart by ID
    public void deleteCartById(UUID cartId) {
        ArrayList<Cart> carts = getCarts();

        for (int i = 0; i < carts.size(); i++) {
            if (carts.get(i).getId().equals(cartId)) {
                carts.remove(i);
                saveAll(carts);
                return;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
    }
}