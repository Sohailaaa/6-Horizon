package com.example.MiniProject1;

import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.UserService;
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

    //TestAddOrderToUser

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
}

