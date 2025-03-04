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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final CartService cartService;
    private final ProductService productService;

    @Autowired
    public UserController(UserService userService, CartService cartService, ProductService productService) {
        this.userService = userService;
        this.cartService = cartService;
        this.productService = productService;
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
        System.out.println("yarab");

       Cart cart= cartService.getCartByUserId(userId);
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
        UUID cartId = cartService.getCartByUserId(userId).getId();
        if (cartId == null) {
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