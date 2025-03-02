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
    public void override(User user){
        ArrayList<User> currentUser = new ArrayList<>();
        currentUser.add(user);
        overrideData(currentUser);
    }

    public ArrayList<User> getUsers() {
        try {
            ArrayList<User> users = findAll();
            return users != null ? users : new ArrayList<>(); // Ensure it never returns null
        } catch (RuntimeException e) {
            System.err.println("Error retrieving users: " + e.getMessage());
            return new ArrayList<>(); // Return empty list in case of an error
        }
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
        ArrayList<User> allUsers = findAll();
        if (allUsers.contains(user)) {
            throw new IllegalArgumentException("User already exists");
        }
        save(user);
        for(User userr:getUsers()){
            System.out.print("saved user"+userr);

        }
        return user;
    }


    public List<Order> getOrdersByUserId(UUID userId) {
        User user = getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user.getOrders();
    }


    public void addOrderToUser(UUID userId, Order order) {
        User user = getUserById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (user.getOrders() == null) {
            user.setOrders(new ArrayList<>());
        }
        System.out.println(" order"+order);
        user.getOrders().add(order);
        orderRepository.addOrder(order);

        emptyCart(userId);
       // save(user);
        override(user);
        System.out.println("saved order"+getOrdersByUserId(userId).size());

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