package backend.controller;

import backend.service.PushyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/device")
@CrossOrigin(origins = "*")
public class DeviceController {

    private final PushyService pushyService;

    public DeviceController(PushyService pushyService) {
        this.pushyService = pushyService;
    }

    @PostMapping("/register")
    public String register(@RequestParam String token) {
        System.out.println("Received device token: " + token);
        pushyService.storeToken(token);
        return "Token registered";
    }
}
