package backend.controller;

import backend.dto.LoginRequest;
import backend.dto.RegistrationRequest;
import backend.entity.User;
import backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Tag(name = "Authentication", description = "Endpoints for user login and registration")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    @Operation(summary = "Redirect to Swagger UI")
    public RedirectView redirectToSwagger() {
        return new RedirectView("/swagger-ui/index.html");
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Login using username and password")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsernameOrEmail());
        
        Map<String, String> response = new HashMap<>();
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(loginRequest.getPassword())) {
            response.put("message", "Login successful for user: " + loginRequest.getUsernameOrEmail());
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Invalid username or password");
            return ResponseEntity.status(401).body(response);
        }
    }

    @PostMapping("/register")
    @Operation(summary = "User Registration", description = "Register a new user with personal details")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegistrationRequest registrationRequest) {
        if (userRepository.findByUsername(registrationRequest.getUsername()).isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Username already exists");
            return ResponseEntity.badRequest().body(response);
        }

        User user = new User();
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());
        user.setContactNumber(registrationRequest.getContactNumber());
        user.setAddress(registrationRequest.getAddress());
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(registrationRequest.getPassword()); // In a real app, password should be encoded

        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User " + registrationRequest.getUsername() + " registered successfully!");
        return ResponseEntity.ok(response);
    }
}
