package com.example.service;

import com.example.model.Order;
import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class UserService extends MainService<User> {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //The Dependency Injection Variables
//The Constructor with the requried variables mapping the Dependency Injection.
    public User addUser(User user) {
        return userRepository.addUser(user);
    }

    public ArrayList<User> getUsers() {
        return null;
    }

    public User getUserById(UUID userId) {
        return null;
    }

    public List<Order> getOrdersByUserId(UUID userId) {
        return null;
    }

    public void addOrderToUser(UUID userId) {
    }

    public void emptyCart(UUID userId) {
    }

    public void removeOrderFromUser(UUID userId, UUID orderId) {
    }

    public void deleteUserById(UUID userId) {
    }

}