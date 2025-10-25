package com.auth.Auth.Service.service.impl;

import com.auth.Auth.Service.dto.response.SignupResponse;
import com.auth.Auth.Service.entity.User;
import com.auth.Auth.Service.mapper.UserMapper;
import com.auth.Auth.Service.repository.UserRepository;
import com.auth.Auth.Service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public ResponseEntity<User> deleteUserById(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Id not found"));
        userRepository.deleteById(userId);
        return ResponseEntity.ok(user);
    }

    @Override
    @Transactional
    public ResponseEntity<User> deleteUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("Username not found"));
        userRepository.deleteByUsername(username);
        return ResponseEntity.ok(user);
    }

    @Override
    @Transactional
    public ResponseEntity<User> deleteUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("Email not Found!"));
        userRepository.deleteByEmail(email);
        return ResponseEntity.ok(user);
    }

    @Override
    @Transactional
    public ResponseEntity<User> deleteUserByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(()-> new RuntimeException("Phone Number not Found!"));
        userRepository.deleteByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(user);
    }


    @Override
    public ResponseEntity<SignupResponse> getById(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        Optional<User> user = userRepository.findById(userId);
        return user.map(u -> ResponseEntity.ok(UserMapper.toResponse(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<SignupResponse> getByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(u -> ResponseEntity.ok(UserMapper.toResponse(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<SignupResponse> getByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(u -> ResponseEntity.ok(UserMapper.toResponse(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<SignupResponse> getByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        return user.map(u -> ResponseEntity.ok(UserMapper.toResponse(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @Override
    @Transactional
    public SignupResponse updateById(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        Optional<User> updateUser = userRepository.findById(userId);
        if (updateUser.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        
        User user = updateUser.get();
        user.setPassword(user.getPassword());
        user.setUsername(user.getUsername());
        user.setEmail(user.getEmail());
        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional
    public SignupResponse updateByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        Optional<User> updateUser = userRepository.findByUsername(username);
        if (updateUser.isEmpty()) {
            throw new RuntimeException("User not found with username: " + username);
        }
        
        User user = updateUser.get();
        user.setPassword(user.getPassword());
        user.setUsername(user.getUsername());
        user.setEmail(user.getEmail());
        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional
    public SignupResponse updateByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        Optional<User> updateUser = userRepository.findByEmail(email);
        if (updateUser.isEmpty()) {
            throw new RuntimeException("User not found with email: " + email);
        }
        
        User user = updateUser.get();
        user.setPassword(user.getPassword());
        user.setUsername(user.getUsername());
        user.setEmail(user.getEmail());
        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional
    public SignupResponse updateByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        
        Optional<User> updateUser = userRepository.findByPhoneNumber(phoneNumber);
        if (updateUser.isEmpty()) {
            throw new RuntimeException("User not found with phone number: " + phoneNumber);
        }
        
        User user = updateUser.get();
        user.setPassword(user.getPassword());
        user.setUsername(user.getUsername());
        user.setEmail(user.getEmail());
        return UserMapper.toResponse(user);
    }
}
