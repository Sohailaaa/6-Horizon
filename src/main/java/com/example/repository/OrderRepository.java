package com.example.repository;

import com.example.model.Order;
import com.example.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class OrderRepository extends MainRepository<Order> {
    @Override
    protected String getDataPath() {
        return "src/main/java/com/example/data/orders.json";

    }

    @Override
    protected Class<Order[]> getArrayType() {
        return Order[].class;
    }

    public OrderRepository() {
    }

    public void addOrder(Order order) {
        save(order);

    }
//amory
    public ArrayList<Order> getOrders() {
        return null;
    }
//amory
    public Order getOrderById(UUID orderId) {
        return null;
    }
//amory
    public void deleteOrderById(UUID orderId) {

    }

}