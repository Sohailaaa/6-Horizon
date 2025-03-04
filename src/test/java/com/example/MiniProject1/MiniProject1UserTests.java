package com.example.MiniProject1;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.CartRepository;
import com.example.repository.OrderRepository;
import com.example.repository.UserRepository;
import com.example.service.CartService;
import com.example.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class MiniProject1UserTests {

    @Autowired
    private UserService userService; // Use actual service

    @Autowired
    private UserRepository userRepository; // Use actual repository

    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private OrderRepository orderRepositoryMock;

    @Mock
    private CartRepository cartRepositoryMock;
    @Autowired
    private CartService cartService;
    @Autowired
    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        userRepository.saveAll(new ArrayList<>()); // Ensure repository starts empty
    }

    //TestAddUSer
    @Test
    void testAddUser_NullUser_ShouldThrowException() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.addUser(null));

        // Print output
        System.out.println("Exception caught: " + exception.getMessage());

        // Verify exception message
        assertEquals("User cannot be null", exception.getMessage());
    }

    @Test
    void testAddUser_ExistingUser_ShouldThrowException() {
        // Arrange
        User existingUser = new User(UUID.randomUUID(), "John Doe", new ArrayList<>());
        userRepository.save(existingUser); // Save user to repository

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.addUser(existingUser));

        // Print output
        System.out.println("Exception caught: " + exception.getMessage());

        // Verify exception message
        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    void testAddUser_NewUser_ShouldBeSavedSuccessfully() {
        // Arrange
        User newUser = new User(UUID.randomUUID(), "Jane Doe", new ArrayList<>());

        // Act
        User savedUser = userService.addUser(newUser);

        // Print output
        System.out.println("User added successfully: " + savedUser);

        // Assert
        assertNotNull(savedUser);
        assertEquals("Jane Doe", savedUser.getName());
        assertTrue(userRepository.findAll().contains(savedUser)); // Ensure user is saved
    }

    //TestGetUsers
    @Test
    void testGetUsers_NoUsers_ShouldReturnEmptyList() {
        List<User> users = userService.getUsers();
        assertNotNull(users, "Users list should not be null");
        assertTrue(users.isEmpty(), "Expected an empty list when no users exist");
    }

    @Test
    void testGetUsers_WithUsers_ShouldReturnListOfUsers() {
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();

        List<Order> user1Orders = List.of(new Order(user1Id, 20.0, List.of(new Product("Product A", 10.0))));
        List<Order> user2Orders = List.of(new Order(user2Id, 30.0, List.of(new Product("Product B", 15.0))));

        User user1 = new User("Alice", user1Orders);
        User user2 = new User("Bob", user2Orders);

        userService.addUser(user1);
        userService.addUser(user2);

        List<User> users = userService.getUsers();

        assertNotNull(users, "Users list should not be null");
        assertEquals(2, users.size(), "Expected 2 users in the list");
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("Alice")), "User Alice should be in the list");
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("Bob")), "User Bob should be in the list");
    }

    @Test
    void testGetUsers_ExceptionThrown_ShouldReturnEmptyList() {
        try {
            List<User> users = userService.getUsers();
            assertNotNull(users, "Users list should not be null");
            assertTrue(users.isEmpty(), "Expected an empty list when no data is present");
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
//TestGetUSerByID

    @Test
    void testGetUserById_ValidId_ShouldReturnUser() {
        // Arrange: Create and save a user inside this test only
        UUID userId = UUID.randomUUID();
        List<Order> orders = List.of(new Order(userId, 50.0, new ArrayList<>()));
        User testUser = new User(userId, "Alice", orders);

        userService.addUser(testUser);

        // Act
        User retrievedUser = userService.getUserById(userId);

        // Assert
        assertNotNull(retrievedUser, "User should not be null for a valid ID");
        assertEquals(userId, retrievedUser.getId(), "Returned user ID should match the requested ID");
        assertEquals("Alice", retrievedUser.getName(), "Returned user should be Alice");
    }

    @Test
    void testGetUserById_UserNotFound_ShouldThrowRuntimeException() {
        // Arrange
        UUID nonExistentUserId = UUID.randomUUID();
        Mockito.when(userRepositoryMock.getUserById(nonExistentUserId)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.getUserById(nonExistentUserId));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetUserById_NullUserId_ShouldThrowIllegalArgumentException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> userService.getUserById(null));
    }

    //TestgetOrderByUserId

    @Test
    void testGetOrdersByUserId_UserExistsWithOrders_ShouldReturnOrders() {
        // Arrange
        UUID userId = UUID.randomUUID();
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(userId, 100.0, new ArrayList<>()));

        User user = new User(userId, "Test User", orders);
        userService.addUser(user);

        when(userRepositoryMock.getUserById(userId)).thenReturn(user);

        // Act
        List<Order> actualOrders = userService.getOrdersByUserId(userId);

        // Assert
        assertNotNull(actualOrders);
        assertEquals(1, actualOrders.size());
        assertEquals(100.0, actualOrders.get(0).getTotalPrice());
    }

    @Test
    void testGetOrdersByUserId_UserNotFound_ShouldThrowRuntimeException() {
        UUID nonExistentUserId = UUID.randomUUID();
        when(userRepositoryMock.getUserById(nonExistentUserId)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.getOrdersByUserId(nonExistentUserId));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetOrdersByUserId_NullUserId_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.getOrdersByUserId(null));

        assertEquals("User cannot be null", exception.getMessage());
    }

    @Test
    void testGetOrdersByUserId_UserExistsWithEmptyOrderList_ShouldReturnEmptyList() {
        UUID userId = UUID.randomUUID();
        List<Order> emptyOrders = new ArrayList<>();
        User user = new User(userId, "Test User", emptyOrders);
        userService.addUser(user);

        when(userRepositoryMock.getUserById(userId)).thenReturn(user);
        when(userRepositoryMock.getOrdersByUserId(userId)).thenReturn(emptyOrders);

        List<Order> actualOrders = userService.getOrdersByUserId(userId);

        assertNotNull(actualOrders);
        assertTrue(actualOrders.isEmpty());
    }

    //RemoveOrderFromUser
    @Test
    void removeOrderFromUser_withValidUserAndSingleOrder_ShouldRemoveOrderSuccessfully() {
        //Arrange
        User user = new User(UUID.randomUUID(), "TestUser", new ArrayList<>());
        Order order = new Order(UUID.randomUUID(), user.getId(), 100.0, new ArrayList<>());
        Cart cart = new Cart(user.getId(), new ArrayList<>());
        userRepository.addUser(user);
        cartRepository.addCart(cart);
        userRepository.addOrderToUser(user.getId(), order);

        //Act
        userService.removeOrderFromUser(user.getId(), order.getId());

        //Assert
        assertTrue(userRepository.getOrdersByUserId(user.getId()).isEmpty());
    }

    @Test
    void removeOrderFromUser_withInvalidOrder_ShouldThrowException() {
        // Arrange
        User user = new User(UUID.randomUUID(), "TestUser", new ArrayList<>());
        Cart cart = new Cart(user.getId(), new ArrayList<>());
        Order order = new Order(UUID.randomUUID(), user.getId(), 100.0, new ArrayList<>());
        userRepository.addUser(user);
        cartRepository.addCart(cart);
        userRepository.addOrderToUser(user.getId(), order);
        UUID invalidOrderId = UUID.randomUUID();

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.removeOrderFromUser(user.getId(), invalidOrderId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Order not found", exception.getReason());
    }

    @Test
    void removeOrderFromUser_withInvalidUser_shouldThrowException() {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.removeOrderFromUser(userId, UUID.randomUUID());
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("User not found", exception.getReason());
    }

    //DeleteUSerById
    @Test
    void deleteUserById_withValidId_shouldDeleteUser() {
        // Arrange
        User user = new User(UUID.randomUUID(), "TestUser", new ArrayList<>());
        userRepository.save(user);

        // Act
        userService.deleteUserById(user.getId());

        // Assert
        assertFalse(userRepository.getUsers().contains(user), "User should be removed from the list");
    }

    @Test
    void deleteUserById_withInvalidId_shouldThrowException() {
        // Arrange
        UUID invalidUserId = UUID.randomUUID();
        User user = new User(UUID.randomUUID(), "TestUser", new ArrayList<>());
        userRepository.save(user);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.deleteUserById(invalidUserId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("User not found", exception.getReason());
    }

    @Test
    void deleteUserById_whenUserListIsEmpty_shouldThrowException() {
        // Arrange
        UUID invalidUserId = UUID.randomUUID();

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.deleteUserById(invalidUserId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("User not found", exception.getReason());
    }

    // TestEmptyCart

    @Test
    void testEmptyCart_CartExistsWithProducts_ShouldRemoveProducts() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "TestUser", new ArrayList<>());
        List<Product> products = new ArrayList<>(Arrays.asList(
                new Product("Laptop", 1000),
                new Product("Phone", 500)
        ));
        Cart cart = new Cart(userId, products);
        userRepository.addUser(user);


        cartService.addCart(cart);  // Ensure cartService is used properly

        when(cartRepositoryMock.getCartByUserId(userId)).thenReturn(cart);
        userService.emptyCart(userId);
        // ✅ Correct assertion: Check if the cart is empty after calling emptyCart
        assertTrue(cartService.getCartByUserId(userId).getProducts().isEmpty());
    }


    @Test
    void testEmptyCart_CartExistsWithNoProducts_ShouldDoNothing() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "TestUser", new ArrayList<>());
        Cart cart = new Cart(userId, new ArrayList<>());
        userRepository.addUser(user);
        cartService.addCart(cart);
        when(cartRepositoryMock.getCartByUserId(userId)).thenReturn(cart);

        userService.emptyCart(userId);

        verify(cartRepositoryMock, never()).deleteProductFromCart(any(), any());
    }

    @Test
    void testEmptyCart_CartDoesNotExist_ShouldThrowNotFound() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "TestUser", new ArrayList<>());
        userRepository.addUser(user);

        when(cartRepositoryMock.getCartByUserId(userId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            userService.emptyCart(userId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Cart not found", exception.getReason());

        verify(cartRepositoryMock, never()).deleteProductFromCart(any(), any());
    }
    
    // TestOrderToUser


    @Test
    void testAddOrderToUser_UserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepositoryMock.getUserById(userId)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.addOrderToUser(userId);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testAddOrderToUser_CartEmpty() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "Test User", new ArrayList<>());
        Cart cart = new Cart(userId, new ArrayList<Product>());
        userRepository.addUser(user);
        cartService.addCart(cart);
        userService.addOrderToUser(userId);

        assertEquals(userRepository.getOrdersByUserId(userId).size(), 0);

    }

    @Test
    void testAddOrderToUser_Success() {

        UUID userId = UUID.randomUUID();
        User user = new User(userId, "Test User", new ArrayList<>());
        Product product = new Product("Test Product", 10.0);
        Cart cart = new Cart(userId, new ArrayList<>(Arrays.asList(product)));
        userRepository.addUser(user);
        cartService.addCart(cart);
        userService.addOrderToUser(userId);

        assertEquals(userRepository.getOrdersByUserId(userId).size(), 1);

    }


}
