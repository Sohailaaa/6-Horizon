package com.example.MiniProject1;

import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
}

