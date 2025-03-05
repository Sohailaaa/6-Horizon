package com.example.controller;

import com.example.exception.DuplicateUserException;
import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.service.CartService;
import com.example.service.ProductService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;

    @Autowired
    public UserController(UserService userService, ProductService productService, CartService cartService) {
        this.userService = userService;
        this.productService = productService;
        this.cartService = cartService;
    }

    @PostMapping("/")
    public User addUser(@RequestBody User user) throws DuplicateUserException, NullPointerException {
        return userService.addUser(user);
    }

    @GetMapping("/")
    public ArrayList<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable UUID userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/{userId}/orders")
    public List<Order> getOrdersByUserId(@PathVariable UUID userId) {
        return userService.getOrdersByUserId(userId);
    }

    @PostMapping("/{userId}/checkout")
    public String addOrderToUser(@PathVariable UUID userId) {
        userService.addOrderToUser(userId);
        return "Order added successfully";

    }

    @PostMapping("/{userId}/removeOrder")
    public String removeOrderFromUser(@PathVariable UUID userId, @RequestParam UUID orderId) {
        userService.removeOrderFromUser(userId, orderId);
        return "Order removed successfully";
    }

    @DeleteMapping("/{userId}/emptyCart")
    public String emptyCart(@PathVariable UUID userId) {

        userService.emptyCart(userId);
        return "Cart emptied successfully";

    }

    @PutMapping("/addProductToCart")
    public String addProductToCart(@RequestParam UUID userId, @RequestParam UUID productId) {
//        Product product = productService.getProductById(productId);
//        Cart cart = cartService.getCartByUserId(userId);
//        UUID cartId = cart.getId();
//        cartService.addProductToCart(cartId, product);
        Cart cart;

        try {
            cart = cartService.getCartByUserId(userId);
        } catch (ResponseStatusException e) {
            cart = new Cart(userId, new ArrayList<Product>());
            cartService.addCart(cart);
        }
        Product product = productService.getProductById(productId);
        if (cart == null) {
            return "Cart is empty";
        }
        if (product == null) {
            return "Product does not exist";
        }

        cartService.addProductToCart(cart.getId(), product);
        return "Product added to cart";
    }

    @PutMapping("/deleteProductFromCart")
    public String deleteProductFromCart(@RequestParam UUID userId, @RequestParam UUID productId) {
//        Product product = productService.getProductById(productId);
//        Cart cart = cartService.getCartByUserId(userId);
//        UUID cartId = cart.getId();
//        cartService.deleteProductFromCart(cartId, product);
        UUID cartId;
        try {
            cartId = cartService.getCartByUserId(userId).getId();
        } catch (ResponseStatusException e) {
            return "Cart is empty";
        }
        if (cartService.getCartByUserId(userId).getProducts().isEmpty()) {
            return "Cart is empty";

        }
        if (productService.getProductById(productId) == null) {
            return "Product does not exist";
        }
        Product product = productService.getProductById(productId);
        cartService.deleteProductFromCart(cartId, product);
        return "Product deleted from cart";
    }

    @DeleteMapping("/delete/{userId}")
    public String deleteUserById(@PathVariable UUID userId) {
        try {
            userService.deleteUserById(userId);
            return "User deleted successfully";
        } catch (Exception e) {
            return "User not found";
        }
    }
}