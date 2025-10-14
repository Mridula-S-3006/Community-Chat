package com.communitychat.controller;

import com.communitychat.model.dto.LoginRequest;
import com.communitychat.model.dto.SignupRequest;
import com.communitychat.model.entity.User;
import com.communitychat.service.AuthService;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") 
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
    try {
        User user = authService.signup(request);
        Map<String, Object> response = new HashMap<>();
        response.put("token", "dummy-token");
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    try {
        String token = authService.login(request);
        User user = authService.getUserByUsername(request.getUsername());
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
}
