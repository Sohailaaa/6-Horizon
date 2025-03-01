package com.example.repository;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class UserRepository extends MainRepository<User> {
    private final CartRepository cartRepository;

    @Override
    protected String getDataPath() {
        return "src/main/java/com/example/data/users.json";
    }

    @Override
    protected Class<User[]> getArrayType() {
        return User[].class;
    }
    private final OrderRepository orderRepository;
    public UserRepository(OrderRepository orderRepository, CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }

    public ArrayList<User> getUsers() {
           return findAll();
    }

    public User getUserById(UUID userId) {
        List<User> users = getUsers();
        for (User user : users) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    public User addUser(User user) {
        System.out.print("helooo");


        ArrayList<User> allUsers = findAll();
        if (allUsers.contains(user)) {
            throw new IllegalArgumentException("User already exists");
        }

        save(user);
        return user;
    }


    public List<Order> getOrdersByUserId(UUID userId) {
        User user = getUserById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user.getOrders();
    }


    public void addOrderToUser(UUID userId, Order order) {
        User user = getUserById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        user.getOrders().add(order);
        save(user);
        orderRepository.addOrder(order);
        emptyCart(userId);

//
    }

    public void emptyCart(UUID userId) {
        Cart cart = cartRepository.getCartByUserId(userId);
        if (cart == null) {
            return;
        }

        List<Product> products = cart.getProducts();
        if (products == null) {
            return; // Nothing to remove
        }

        for (Product product : products) {
            cartRepository.deleteProductFromCart(cart.getId(), product);
        }
    }


    //nada
    public void removeOrderFromUser(UUID userId, UUID orderId) {
        User user = getUserById(userId);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        List<Order> orders = user.getOrders();

        if (orders != null && !orders.isEmpty()) {
            boolean removed = orders.removeIf(order -> order.getId().equals(orderId));

            if (removed) {
                orderRepository.deleteOrderById(orderId);
                save(user);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
            }
        }
    }
//nada
    public void deleteUserById(UUID userId) {
        ArrayList<User> users = getUsers();
        boolean result = users.removeIf(user -> user.getId().equals(userId));

        if(!result){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        saveAll(users);
    }
}