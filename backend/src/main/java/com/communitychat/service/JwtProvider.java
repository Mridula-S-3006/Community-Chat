package com.communitychat.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {
    private final String jwtSecret = "mySimpleSecretKey123";
    private final long jwtExpirationMs = 86400000; // 24 hours

    public String generateToken(String username) {
        return "demo-token-" + username;
    }
}