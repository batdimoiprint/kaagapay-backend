package backend.controller;

import backend.entity.User;
import backend.logging.LogUtil;
import backend.repository.UserRepository;
import backend.service.PushyService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/device")
@CrossOrigin(origins = "*")
public class DeviceController {

    private static final Logger log = LogUtil.getLogger(DeviceController.class);

    private final PushyService pushyService;
    private final UserRepository userRepository;

    public DeviceController(PushyService pushyService, UserRepository userRepository) {
        this.pushyService = pushyService;
        this.userRepository = userRepository;
    }

    @PostMapping("/send-notification")
    @Operation(summary = "Send Notification", description = "Send a push notification to a target user")
    public ResponseEntity<Map<String, String>> sendNotification(@RequestParam Map<String, String> requestData) {
        Long targetUserId = Long.valueOf(requestData.get("targetUserId"));
        String message = requestData.get("message");

        Optional<User> userOpt = userRepository.findById(targetUserId);
        if (userOpt.isPresent()) {
            pushyService.sendPushNotificationToUser(userOpt.get(), message);
            return ResponseEntity.ok(Map.of("message", "Notification sent successfully"));
        } else {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Register Device", description = "Register a device token for the current user (deprecated, use /login or /device-login)")
    public String register(@RequestParam String token) {
        // Avoid logging full tokens; keep only a short preview.
        String preview = (token != null && token.length() > 16) ? token.substring(0, 16) + "..." : token;
        log.info("Received device token (preview={} length={})", preview, token != null ? token.length() : 0);
        // This old method doesn't have user context, it should probably be removed or updated
        return "Token registered (no user associated)";
    }
}
