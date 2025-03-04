package com.example.service;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.CartRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class UserService extends MainService<User> {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    public UserService(UserRepository userRepository, CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
    }

    //The Dependency Injection Variables
//The Constructor with the requried variables mapping the Dependency Injection.
    public User addUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return userRepository.addUser(user);
    }

    public ArrayList<User> getUsers() {
        return userRepository.getUsers();
    }

    public User getUserById(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user;

    }

    public List<Order> getOrdersByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return userRepository.getOrdersByUserId(userId);
    }

    public void addOrderToUser(UUID userId) {
        getUserById(userId);
        Cart cart = cartRepository.getCartByUserId(userId);
        if (cart == null || cart.getProducts().isEmpty()) {
            return;
        }
        List<Product> products = cart.getProducts();
        double totalPrice = 0;
        for (Product product : products) {
            totalPrice += product.getPrice();
        }
        Order order = new Order(userId, totalPrice, products);
        userRepository.addOrderToUser(userId, order);

    }

    public void emptyCart(UUID userId) {
        userRepository.emptyCart(userId);
    }

    public void removeOrderFromUser(UUID userId, UUID orderId) {
        userRepository.removeOrderFromUser(userId, orderId);
    }

    public void deleteUserById(UUID userId) {
        userRepository.deleteUserById(userId);
    }
}