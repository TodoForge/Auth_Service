package com.auth.Auth.Service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HealthController {

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Auth-Service",
            "message", "Service is running"
        ));
    }
}
