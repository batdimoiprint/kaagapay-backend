package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegistrationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Authentication", description = "Endpoints for user login and registration")
public class AuthController {

    @GetMapping("/")
    @Operation(summary = "Redirect to Swagger UI")
    public RedirectView redirectToSwagger() {
        return new RedirectView("/swagger-ui/index.html");
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Login using username or email and password")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        // Mock login logic
        Map<String, String> response = new HashMap<>();
        response.put("message", "Login successful for user: " + loginRequest.getUsernameOrEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "User Registration", description = "Register a new user with personal details")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegistrationRequest registrationRequest) {
        // Mock registration logic
        Map<String, String> response = new HashMap<>();
        response.put("message", "User " + registrationRequest.getUsername() + " registered successfully!");
        return ResponseEntity.ok(response);
    }
}
