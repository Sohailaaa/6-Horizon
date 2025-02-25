package com.example.repository;

import com.example.model.Order;
import com.example.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class UserRepository extends MainRepository<User> {
    @Override
    protected String getDataPath() {
        return "src/main/java/com/example/data/users.json";
    }

    @Override
    protected Class<User[]> getArrayType() {
        return User[].class;
    }

    public UserRepository() {
    }

    public ArrayList<User> getUsers() {
        return null;
    }

    public User getUserById(UUID userId) {
        return null;
    }

    public User addUser(User user) {
        save(user);
        return user;
    }

    public List<Order> getOrdersByUserId(UUID userId) {
        return null;
    }

    public void addOrderToUser(UUID userId, Order order) {

    }
//nada
    public void removeOrderFromUser(UUID userId, UUID orderId) {

    }
//nada
    public void deleteUserById(UUID userId) {

    }


}