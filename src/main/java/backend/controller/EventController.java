package backend.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
@RestController
@RequestMapping("/event")
@Tag(name = "Event (SSE)", description = "Server-Sent Events endpoints")
public class EventController {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final backend.service.PushyService pushyService;

    public EventController(backend.service.PushyService pushyService) {
        this.pushyService = pushyService;
    }

    @Operation(summary = "Subscribe to SSE", description = "Clients connect here to keep an open connection for Server-Sent Events.")
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(jakarta.servlet.http.HttpServletResponse response) {
        response.setContentType("text/event-stream");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // Keep connection open indefinitely
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        try {
            // Send an initial dummy event to establish the connection properly and send the correct headers immediately
            emitter.send(SseEmitter.event().name("init").data("connected"));
        } catch (IOException e) {
            emitters.remove(emitter);
        }

        return emitter;
    }

    @Operation(summary = "Trigger SSE Event", description = "Send a custom alert message to all connected SSE clients and via push notification.")
    @PostMapping()
    public String triggerEvent(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Message to send",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(example = "{\"message\": \"check-in\"}")
                    )
            )
            @RequestBody(required = false) Map<String, String> body) {
        String message = (body != null && body.containsKey("message")) ? body.get("message") : "check-in";

        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        emitters.forEach(emitter -> {
            try {
                // SseEmitter automatically formats the output to `data: {"alert": "message"}\n\n`
                emitter.send(SseEmitter.event()
                        .data("{\"alert\": \"" + message + "\"}"));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
        pushyService.sendPushNotification(message);
        return "Event triggered successfully to " + emitters.size() + " subscribers";
    }
}
