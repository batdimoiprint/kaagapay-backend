package backend.controller;

import backend.dto.LoginRequest;
import backend.dto.RegistrationRequest;
import backend.entity.User;
import backend.repository.UserRepository;
import backend.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "Redirects to Swagger documentation API interface")
    })
    public RedirectView redirectToSwagger() {
        return new RedirectView("/swagger-ui/index.html");
    }

    @PostMapping(value = "/login")
    @Operation(summary = "User Login", description = "Login using username and password (form-encoded or query params) to get JWT tokens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful. Returns access token, refresh token in cookies, and user ID."),
        @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid username or password.")
    })
    public ResponseEntity<Map<String, String>> login(@ModelAttribute LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsernameOrEmail());

        Map<String, String> response = new HashMap<>();
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(loginRequest.getPassword())) {
            User user = userOpt.get();
            String accessToken = jwtService.generateToken(user.getId(), user.getUsername());
            String refreshToken = jwtService.generateRefreshToken(user.getUsername());

            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .sameSite("Lax")
                    .maxAge(jwtService.getJwtExpiration() / 1000)
                    .build();

            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .sameSite("Lax")
                    .maxAge(jwtService.getRefreshExpiration() / 1000)
                    .build();
            response.put("message", "Login successful");
            response.put("userId", String.valueOf(user.getId()));            
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(response);
        } else {
            response.put("message", "Invalid username or password");
            return ResponseEntity.status(401).body(response);
        }
    }

    @PostMapping(value = "/register")
    @Operation(summary = "User Registration", description = "Register a new user with personal details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Bad Request. Username already exists or invalid data.")
    })
    public ResponseEntity<Map<String, String>> register(@ModelAttribute RegistrationRequest registrationRequest) {
        if (userRepository.findByUsername(registrationRequest.getUsername()).isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Username already exists");
            return ResponseEntity.badRequest().body(response);
        }

        User user = new User();
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());
        user.setContactNumber(registrationRequest.getContactNumber());
        user.setEmail(registrationRequest.getEmail());
        user.setSubdivision(registrationRequest.getSubdivision());
        user.setStreetName(registrationRequest.getStreetName());
        user.setStreetNo(registrationRequest.getStreetNo());
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(registrationRequest.getPassword());

        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User " + registrationRequest.getUsername() + " registered successfully!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh Access Token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Access token refreshed successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or expired refresh token.")
    })
    public ResponseEntity<Map<String, String>> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        
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

    @GetMapping("/me")
    @Operation(summary = "Get Current User Information", description = "Returns user information based on the provided access token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user information"),
        @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing token."),
        @ApiResponse(responseCode = "404", description = "User not found.")
    })
    public ResponseEntity<?> getCurrentUser(@CookieValue(value = "accessToken", required = false) String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        
        try {
            String username = jwtService.extractUsername(accessToken);
            
            if (username != null && jwtService.isTokenValid(accessToken, username)) {
                Optional<User> userOpt = userRepository.findByUsername(username);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", user.getId());
                    userInfo.put("username", user.getUsername());
                    userInfo.put("firstName", user.getFirstName());
                    userInfo.put("lastName", user.getLastName());
                    userInfo.put("email", user.getEmail());
                    userInfo.put("contactNumber", user.getContactNumber());
                    userInfo.put("subdivision", user.getSubdivision());
                    userInfo.put("streetName", user.getStreetName());
                    userInfo.put("streetNo", user.getStreetNo());
                    return ResponseEntity.ok(userInfo);
                } else {
                    return ResponseEntity.status(404).body(Map.of("message", "User not found"));
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));
        }
        
        return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
    }
}
