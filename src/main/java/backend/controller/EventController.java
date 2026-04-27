package backend.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
@RestController
@RequestMapping("/event")
@Tag(name = "Event (SSE)", description = "Server-Sent Events endpoints")
public class EventController {

    private final backend.service.EventService eventService;

    public EventController(backend.service.EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(summary = "Subscribe to SSE", description = "Clients connect here to keep an open connection for Server-Sent Events.")
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(jakarta.servlet.http.HttpServletResponse response) {
        response.setContentType("text/event-stream");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        
        return eventService.subscribe();
    }

    @Operation(summary = "Trigger SSE Event", description = "Send a custom alert message to all connected SSE clients and via push notification.")
    @SecurityRequirement(name = "accessTokenCookie")
    @PostMapping
    public ResponseEntity<String> triggerEvent(
            Authentication authentication,
            @io.swagger.v3.oas.annotations.Parameter(description = "Message to send (optional, defaults to 'check-in')")
            @RequestParam(required = false) String message) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized: Missing or invalid access token");
        }
        
        String alertMessage = "check-in";
        if (message != null && !message.isEmpty()) {
            alertMessage = message;
        }

        int count = eventService.broadcast(alertMessage);
        return ResponseEntity.ok(
                "Event triggered successfully by " + authentication.getName() + " to " + count + " subscribers"
        );
    }
}
