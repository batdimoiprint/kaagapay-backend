package backend.controller;

import backend.model.Greeting;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health Check / Greeting", description = "Basic test endpoints")
public class HelloController {
    
    @GetMapping("/hello")
    @Operation(summary = "Returns a simple greeting message")
    @ApiResponse(responseCode = "200", description = "Greeting successfully returned")
    public Greeting hello() {
        return new Greeting("Hello from Spring Boot on Heroku!");
    }
}
