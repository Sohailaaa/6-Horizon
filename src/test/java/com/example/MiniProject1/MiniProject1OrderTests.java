package com.example.MiniProject1;

import com.example.model.Order;
import com.example.repository.OrderRepository;
import com.example.repository.UserRepository;
import com.example.service.OrderService;
import com.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MiniProject1OrderTests {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order sampleOrder;
    private UUID sampleOrderId;

    @BeforeEach
    void setUp() {
        sampleOrderId = UUID.randomUUID();
        sampleOrder = new Order(sampleOrderId, UUID.randomUUID(), 100.0, new ArrayList<>());
    }

    //Tests addOrder
    // 1
    @Test
    void testAddOrder_Success() {
        doNothing().when(orderRepository).addOrder(sampleOrder);
        assertDoesNotThrow(() -> orderService.addOrder(sampleOrder));
        verify(orderRepository, times(1)).addOrder(sampleOrder);
    }

    //2
    @Test
    void testAddOrder_NullOrder() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> orderService.addOrder(null));

        assertEquals("Order cannot be null", exception.getMessage());

        verify(orderRepository, never()).addOrder(any());
    }
    //3
    @Test
    void testAddOrder_NegativePrice_ShouldThrowException() {
        Order negativePriceOrder = new Order(UUID.randomUUID(), UUID.randomUUID(), -50.0, new ArrayList<>());
        assertThrows(IllegalArgumentException.class, () -> orderService.addOrder(negativePriceOrder));
        verify(orderRepository, never()).addOrder(any(Order.class));
    }
    // 4 for securing it :D
    @Test
    void testAddOrder_HighPrice() {
        Order highPriceOrder = new Order(UUID.randomUUID(), UUID.randomUUID(), Double.MAX_VALUE, new ArrayList<>());
        doNothing().when(orderRepository).addOrder(highPriceOrder);
        assertDoesNotThrow(() -> orderService.addOrder(highPriceOrder));
    }
    // Tests for getOrders
    // 1
    @Test
    void testGetOrders_Success() {
        List<Order> orders = List.of(sampleOrder);
        when(orderRepository.getOrders()).thenReturn(new ArrayList<>(orders));
        List<Order> result = orderService.getOrders();
        assertEquals(1, result.size());
        assertEquals(sampleOrder, result.get(0));
    }

    // 2
    @Test
    void testGetOrders_EmptyList() {
        when(orderRepository.getOrders()).thenReturn(new ArrayList<>());
        List<Order> result = orderService.getOrders();
        assertTrue(result.isEmpty(), "Expected an empty list when no orders exist.");
    }
    //3
    @Test
    void testGetOrders_RepositoryFailure() {
        when(orderRepository.getOrders()).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> orderService.getOrders());

        assertEquals("Could not retrieve orders", exception.getMessage());
    }

    //4 to secure and future :D
    @Test
    void testGetOrders_LargeDataset() {
        List<Order> largeList = new ArrayList<>();
        for (int i = 0; i < 10_000; i++) {
            largeList.add(new Order(UUID.randomUUID(), UUID.randomUUID(), Math.random() * 500, new ArrayList<>()));
        }
        when(orderRepository.getOrders()).thenReturn(new ArrayList<>(largeList));
        List<Order> result = orderService.getOrders();
        assertEquals(10_000, result.size(), "Expected 10,000 orders.");
    }

    //Tests for getOrderById
    // 1
    @Test
    void testGetOrderById_Success() {
        when(orderRepository.getOrderById(sampleOrderId)).thenReturn(sampleOrder);

        Order result = orderService.getOrderById(sampleOrderId);

        assertNotNull(result, "Order should not be null.");
        assertEquals(sampleOrder, result, "The returned order should match the expected order.");
        assertEquals(sampleOrder.getId(), result.getId(), "Order ID should match.");
    }

    // 2
    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.getOrderById(sampleOrderId)).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> orderService.getOrderById(sampleOrderId));

        assertEquals("Order not found", exception.getMessage());
    }

    // 3
    @Test
    void testGetOrderById_InvalidUUID() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> orderService.getOrderById(null));

        assertEquals("Order ID cannot be null", exception.getMessage());
    }
    // Test DeleteOrderByID
    //1
    @Test
    void testDeleteOrderById_Success() {
        when(orderRepository.getOrderById(sampleOrderId)).thenReturn(sampleOrder);
        doNothing().when(orderRepository).deleteOrderById(sampleOrderId);
        assertDoesNotThrow(() -> orderService.deleteOrderById(sampleOrderId));
        verify(orderRepository, times(1)).deleteOrderById(sampleOrderId);
    }
    //2
    @Test
    void testDeleteOrderById_NotFound() {
        when(orderRepository.getOrderById(sampleOrderId)).thenReturn(null);
        Exception exception = assertThrows(NullPointerException.class, () -> orderService.deleteOrderById(sampleOrderId));
        assertEquals("Order cannot be null", exception.getMessage());
        verify(orderRepository, never()).deleteOrderById(sampleOrderId);
    }
    //3
    @Test
    void testDeleteOrderById_NullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> orderService.deleteOrderById(null));
        assertEquals("Order ID cannot be null", exception.getMessage());
    }
}

