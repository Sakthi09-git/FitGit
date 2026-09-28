package com.fitlog.service;

import com.fitlog.dto.CreateUserRequest;
import com.fitlog.dto.UpdateUserRequest;
import com.fitlog.dto.UserResponse;
import com.fitlog.entity.User;
import com.fitlog.exception.BusinessRuleException;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service handling User management and business rules.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(CreateUserRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new BusinessRuleException("Email cannot be empty.");
        }
        if (userRepository.existsByEmail(request.getEmail().trim())) {
            throw new BusinessRuleException("User with email '" + request.getEmail() + "' already exists.");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword());
        user.setActive(true);

        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = getUserEntityById(id);
        return UserResponse.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = getUserEntityById(id);

        if (!user.getEmail().equalsIgnoreCase(request.getEmail().trim())) {
            if (userRepository.existsByEmail(request.getEmail().trim())) {
                throw new BusinessRuleException("User with email '" + request.getEmail() + "' already exists.");
            }
            user.setEmail(request.getEmail().trim().toLowerCase());
        }

        user.setName(request.getName().trim());
        user.setPhone(request.getPhone());
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        User updatedUser = userRepository.save(user);
        return UserResponse.fromEntity(updatedUser);
    }

    public void deleteUser(Long id) {
        User user = getUserEntityById(id);
        // Soft-deactivation or full deletion
        userRepository.delete(user);
    }
}
