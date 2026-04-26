package backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PushyService {

    @Value("${pushy.secret.api.key}")
    private String secretApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // Store token in memory. Replace with DB in production.
    private String deviceToken;

    public void storeToken(String token) {
        this.deviceToken = token;
    }

    public void sendPushNotification(String alert) {
        if (deviceToken == null) return;

        String url = "https://api.pushy.me/push?api_key=" + secretApiKey;

        Map<String, Object> data = new HashMap<>();
        data.put("alert", alert);

        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "Alert");
        notification.put("body", alert);

        Map<String, Object> body = new HashMap<>();
        body.put("to", deviceToken);
        body.put("data", data);
        body.put("notification", notification);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, request, String.class);
    }
}
