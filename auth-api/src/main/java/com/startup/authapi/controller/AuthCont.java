package com.startup.authapi.controller;

import com.startup.authapi.config.JwtUtil;
import com.startup.authapi.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AuthCont {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthCont(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            authService.register(request.get("email"), request.get("password"));
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        Optional<String> tokenOpt = authService.login(request.get("email"), request.get("password"));

        if (tokenOpt.isPresent()) {
            return ResponseEntity.ok(Map.of("token", tokenOpt.get()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    @PostMapping("/process")
    public ResponseEntity<?> process(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {

        String token = authHeader.substring(7);
        String userIdStr = jwtUtil.validateTokenAndGetClaims(token).get("userId", String.class);
        UUID userId = UUID.fromString(userIdStr);

        try {
            String result = authService.processText(userId, request.get("text"));
            return ResponseEntity.ok(Map.of("result", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Service B error");
        }
    }
}