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

    @BeforeEach
    void setUp() {
        userRepository.saveAll(new ArrayList<>()); // Ensure repository starts empty
    }

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
}

