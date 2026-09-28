package com.fitlog.service;

import com.fitlog.dto.CreateUserRequest;
import com.fitlog.dto.UserResponse;
import com.fitlog.entity.User;
import com.fitlog.exception.BusinessRuleException;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private CreateUserRequest createUserRequest;

    @BeforeEach
    void setUp() {
        createUserRequest = new CreateUserRequest("Test User", "test@example.com", "1234567890", "secret");
    }

    @Test
    @DisplayName("Should successfully create a new user")
    void createUser_Success() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

        User savedUser = new User("Test User", "test@example.com", "1234567890", "secret");
        savedUser.setId(10L);
        savedUser.setCreatedAt(LocalDateTime.now());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Test User", response.getName());
        assertEquals("test@example.com", response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when email already exists")
    void createUser_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                userService.createUser(createUserRequest));

        assertTrue(ex.getMessage().contains("already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for non-existent user")
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                userService.getUserById(999L));

        assertTrue(ex.getMessage().contains("User not found"));
    }
}
