package com.communitychat.service;

import com.communitychat.util.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public String generateToken(Long userId) {
        return JwtUtil.generateToken(userId);
    }

    public boolean validateToken(String token) {
        return JwtUtil.validateToken(token);
    }

    public Long getUserIdFromToken(String token) {
        return JwtUtil.getUserIdFromToken(token);
    }
}
