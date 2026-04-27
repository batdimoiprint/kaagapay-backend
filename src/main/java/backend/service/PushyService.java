package backend.service;

import backend.entity.DeviceToken;
import backend.entity.User;
import backend.repository.DeviceTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PushyService {

    @Value("${pushy.secret.api.key}")
    private String secretApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final DeviceTokenRepository deviceTokenRepository;

    public PushyService(DeviceTokenRepository deviceTokenRepository) {
        this.deviceTokenRepository = deviceTokenRepository;
    }

    public void storeToken(User user, String token) {
        deviceTokenRepository.findByToken(token).ifPresentOrElse(
            existingToken -> {
                if (!existingToken.getUser().getId().equals(user.getId())) {
                    existingToken.setUser(user);
                    deviceTokenRepository.save(existingToken);
                }
            },
            () -> {
                DeviceToken newToken = new DeviceToken();
                newToken.setUser(user);
                newToken.setToken(token);
                deviceTokenRepository.save(newToken);
            }
        );
    }

    public void removeToken(String token) {
        deviceTokenRepository.findByToken(token).ifPresent(deviceTokenRepository::delete);
    }

    public void sendPushNotificationToUser(User user, String alert) {
        List<DeviceToken> deviceTokens = deviceTokenRepository.findByUser(user);
        if (deviceTokens.isEmpty()) {
            System.out.println("No device tokens registered for user: " + user.getId());
            return;
        }

        List<String> tokens = deviceTokens.stream()
                .map(DeviceToken::getToken)
                .collect(Collectors.toList());

        sendPushNotification(tokens, alert);
    }

    public void sendPushNotificationToAll(String alert) {
        List<DeviceToken> deviceTokens = deviceTokenRepository.findAll();
        if (deviceTokens.isEmpty()) {
            System.out.println("No device tokens registered.");
            return;
        }

        List<String> tokens = deviceTokens.stream()
                .map(DeviceToken::getToken)
                .collect(Collectors.toList());

        sendPushNotification(tokens, alert);
    }

    private void sendPushNotification(List<String> tokens, String alert) {
        String url = "https://api.pushy.me/push?api_key=" + secretApiKey;

        Map<String, Object> data = new HashMap<>();
        data.put("message", alert);
        data.put("title", "Kaagapay Alert");

        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "Alert");
        notification.put("body", alert);

        Map<String, Object> body = new HashMap<>();
        // Pushy expects 'to' as an array of strings for multiple recipients
        body.put("to", tokens);
        body.put("data", data);
        body.put("notification", notification);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.println("Pushy response: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Error sending push notification: " + e.getMessage());
        }
    }
}
