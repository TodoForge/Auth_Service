package com.auth.Auth.Service.service;

import com.auth.Auth.Service.dto.response.SignupResponse;
import com.auth.Auth.Service.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    List<User> getAllUsers();

    ResponseEntity<User> deleteUserById(UUID userId);
    ResponseEntity<User> deleteUserByUsername(String username);
    ResponseEntity<User> deleteUserByEmail(String email);
    ResponseEntity<User> deleteUserByPhoneNumber(String phoneNumber);

    ResponseEntity<SignupResponse> getById(UUID userId);
    ResponseEntity<SignupResponse> getByUsername(String username);
    ResponseEntity<SignupResponse> getByEmail(String email);
    ResponseEntity<SignupResponse> getByPhoneNumber(String phoneNumber);

    SignupResponse updateById(UUID userId);
    SignupResponse updateByUsername(String username);
    SignupResponse updateByEmail(String email);
    SignupResponse updateByPhoneNumber(String phoneNumber);
    
    // Add updateUser method for UserProfileController
    User updateUser(User user);

}
