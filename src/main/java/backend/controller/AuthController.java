package backend.controller;

import backend.dto.LoginRequest;
import backend.dto.RegistrationRequest;
import backend.entity.User;
import backend.repository.UserRepository;
import backend.security.JwtService;
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

    @Autowired
    private JwtService jwtService;

    @GetMapping("/")
    @Operation(summary = "Redirect to Swagger UI")
    public RedirectView redirectToSwagger() {
        return new RedirectView("/swagger-ui/index.html");
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Login using username and password to get JWT tokens")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsernameOrEmail());
        
        Map<String, String> response = new HashMap<>();
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(loginRequest.getPassword())) {
            User user = userOpt.get();
            String accessToken = jwtService.generateToken(user.getId(), user.getUsername());
            String refreshToken = jwtService.generateRefreshToken(user.getUsername());
            
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("userId", String.valueOf(user.getId()));
            response.put("message", "Login successful");
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
        user.setPassword(registrationRequest.getPassword());

        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User " + registrationRequest.getUsername() + " registered successfully!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh Access Token")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }
        
        String refreshToken = authHeader.substring(7);
        String username = jwtService.extractUsername(refreshToken);
        
        if (username != null && jwtService.isTokenValid(refreshToken, username)) {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                String newAccessToken = jwtService.generateToken(userOpt.get().getId(), username);
                Map<String, String> response = new HashMap<>();
                response.put("accessToken", newAccessToken);
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(401).build();
    }
}
