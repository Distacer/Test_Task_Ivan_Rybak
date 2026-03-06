package com.startup.dataapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class Transformer {

    @Value("${INTERNAL_TOKEN}")
    private String internalToken;

    @PostMapping("/transform")
    public ResponseEntity<Map<String, String>> transform(
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "X-Internal-Token", required = false) String token) {

        if (token == null || !token.equals(internalToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String input = request.get("text");
        String output = new StringBuilder(input).reverse().toString().toUpperCase();
        return ResponseEntity.ok(Map.of("result", output));
    }
}