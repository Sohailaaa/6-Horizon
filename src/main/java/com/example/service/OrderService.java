package com.example.service;

import com.example.model.Order;
import com.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;


@Service
@SuppressWarnings("rawtypes")
public class OrderService extends MainService<Order> {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository repository) {
        this.orderRepository = repository;
    }

    public void addOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (order.getTotalPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        orderRepository.addOrder(order);
    }

    public ArrayList<Order> getOrders() {
        try {
            return orderRepository.getOrders();
        } catch (Exception e) {
            throw new RuntimeException("Could not retrieve orders");
        }
    }

    public Order getOrderById(UUID orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        Order order = orderRepository.getOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }
        return order;
    }

    public void deleteOrderById(UUID orderId) throws IllegalArgumentException {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        Order order = orderRepository.getOrderById(orderId);
        if (order == null) {
            throw new NullPointerException("Order cannot be null");
        }

        orderRepository.deleteOrderById(orderId);
    }
}
