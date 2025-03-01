package com.example.MiniProject1;

import com.example.model.Order;
import com.example.model.User;
import com.example.repository.OrderRepository;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
    @Autowired
    private OrderRepository orderRepository;

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
        Exception exception = assertThrows(ResponseStatusException.class, () -> userService.deleteUserById(invalidUserId));

        assertEquals("404 NOT_FOUND \"User not found\"", exception.getMessage(), "Exception reason should match expected message");
    }

    @Test
    void deleteUserById_whenUserListIsEmpty_shouldThrowException() {
        // Arrange
        UUID invalidUserId = UUID.randomUUID();

        // Act & Assert
        Exception exception = assertThrows(ResponseStatusException.class, () -> userService.deleteUserById(invalidUserId));

        assertEquals("404 NOT_FOUND \"User not found\"", exception.getMessage(), "Exception reason should match expected message");
    }
}

