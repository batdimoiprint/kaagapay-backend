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
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    @Operation(summary = "Redirect to Swagger UI")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "Redirects to Swagger documentation API interface")
    })
    public RedirectView redirectToSwagger() {
        return new RedirectView("/swagger-ui/index.html");
    }

    @Autowired
    private backend.service.PushyService pushyService;

    @PostMapping(value = "/login")
    @Operation(summary = "User Login", description = "Login using username and password to get JWT tokens and register device token")
    @io.swagger.v3.oas.annotations.Parameters({
        @io.swagger.v3.oas.annotations.Parameter(name = "usernameOrEmail", example = "johndoe", description = "Username or Email address"),
        @io.swagger.v3.oas.annotations.Parameter(name = "password", example = "password123", description = "User password"),
        @io.swagger.v3.oas.annotations.Parameter(name = "deviceToken", example = "f8W...z2", description = "Optional device token for push notifications")
    })
    public ResponseEntity<Map<String, String>> login(@RequestParam Map<String, String> loginData) {
        String usernameOrEmail = loginData.get("usernameOrEmail");
        String password = loginData.get("password");
        String deviceToken = loginData.get("deviceToken");

        Optional<User> userOpt = userRepository.findByUsername(usernameOrEmail);

        Map<String, String> response = new HashMap<>();
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            User user = userOpt.get();
            
            if (deviceToken != null && !deviceToken.isEmpty()) {
                pushyService.storeToken(user, deviceToken);
            }

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
            response.put("role", user.getRole());            
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
        user.setAge(registrationRequest.getAge());
        user.setGender(registrationRequest.getGender());
        user.setRole(registrationRequest.getRole() != null && !registrationRequest.getRole().isEmpty() ? registrationRequest.getRole() : "resident");
        user.setEmail(registrationRequest.getEmail());
        user.setSubdivision(registrationRequest.getSubdivision());
        user.setStreetName(registrationRequest.getStreetName());
        user.setStreetNo(registrationRequest.getStreetNo());
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User " + registrationRequest.getUsername() + " registered successfully!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "User Logout", description = "Logs out the user, clears authentication cookies, and optionally removes the device token from the notification service")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout successful. Authentication cookies are cleared."),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Map<String, String>> logout(
            @io.swagger.v3.oas.annotations.Parameter(description = "Optional device token to remove from the push notification service", example = "f8W...z2")
            @RequestParam(required = false) String deviceToken) {
        
        if (deviceToken != null && !deviceToken.isEmpty()) {
            pushyService.removeToken(deviceToken);
        }

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }

    @PostMapping("/device-login")
    @Operation(summary = "Device Login", description = "Associate a user ID with a device token")
    @io.swagger.v3.oas.annotations.Parameters({
        @io.swagger.v3.oas.annotations.Parameter(name = "userId", example = "1", description = "ID of the user"),
        @io.swagger.v3.oas.annotations.Parameter(name = "deviceToken", example = "f8W...z2", description = "Device token to associate")
    })
    public ResponseEntity<Map<String, String>> deviceLogin(@RequestParam Map<String, String> requestData) {
        Long userId = Long.valueOf(requestData.get("userId"));
        String deviceToken = requestData.get("deviceToken");

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            pushyService.storeToken(userOpt.get(), deviceToken);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Device registered successfully");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }
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
                    userInfo.put("age", user.getAge());
                    userInfo.put("gender", user.getGender());
                    userInfo.put("role", user.getRole());
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
