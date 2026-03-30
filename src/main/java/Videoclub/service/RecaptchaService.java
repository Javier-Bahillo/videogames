package Videoclub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecaptchaService {

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    private final RestTemplate restTemplate;

    @Value("${recaptcha.secret}")
    private String secretKey;

    public boolean verify(String token) {
        if (token == null || token.isBlank()) return false;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", secretKey);
        params.add("response", token);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(VERIFY_URL, params, Map.class);
            if (response == null || !Boolean.TRUE.equals(response.get("success"))) return false;

            Object score = response.get("score");
            if (score instanceof Number) {
                return ((Number) score).doubleValue() >= 0.5;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
