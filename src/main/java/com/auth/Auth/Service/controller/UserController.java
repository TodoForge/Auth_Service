package com.auth.Auth.Service.controller;

import com.auth.Auth.Service.dto.response.SignupResponse;
import com.auth.Auth.Service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<SignupResponse> getUserById(@PathVariable UUID id){
        return userService.getById(id);
    }


}
