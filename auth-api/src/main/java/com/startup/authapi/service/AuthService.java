package com.startup.authapi.service;

import com.startup.authapi.config.JwtUtil;
import com.startup.authapi.model.ProcessingLog;
import com.startup.authapi.model.User;
import com.startup.authapi.repo.ProcessingLogRepo;
import com.startup.authapi.repo.UserRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepo userRepo;
    private final ProcessingLogRepo logRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;

    @Value("${INTERNAL_TOKEN:winwin-secret-2026}")
    private String internalToken;

    public AuthService(UserRepo userRepo, ProcessingLogRepo logRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, RestTemplate restTemplate) {
        this.userRepo = userRepo;
        this.logRepo = logRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
    }

    public void register(String email, String password) {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        userRepo.save(user);
    }

    public Optional<String> login(String email, String password) {
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPasswordHash())) {
            return Optional.of(jwtUtil.generateToken(userOpt.get().getEmail(), userOpt.get().getId().toString()));
        }
        return Optional.empty();
    }

    public String processText(UUID userId, String text) {
        String serviceBUrl = "http://data-api:8081/api/transform";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Token", internalToken);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(Map.of("text", text), headers);

        Map<String, String> response = restTemplate.postForObject(serviceBUrl, entity, Map.class);
        if (response == null || !response.containsKey("result")) {
            throw new RuntimeException("Service B returned an empty response");
        }

        String result = response.get("result");

        ProcessingLog log = new ProcessingLog();
        log.setUserId(userId);
        log.setInputText(text);
        log.setOutputText(result);
        log.setCreatedAt(LocalDateTime.now());
        logRepo.save(log);

        return result;
    }
}