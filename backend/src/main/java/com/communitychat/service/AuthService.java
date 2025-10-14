package com.communitychat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.communitychat.model.dto.LoginRequest;
import com.communitychat.model.dto.SignupRequest;
import com.communitychat.model.entity.User;
import com.communitychat.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Autowired
    public AuthService(UserRepository userRepository, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    public User signup(SignupRequest request) {
  
        String username = request.getUsername().trim();
        String password = request.getPassword().trim();
        String email = request.getEmail() != null ? request.getEmail().trim() : null;
        String name = request.getName() != null ? request.getName().trim() : null;

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (email != null && userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setName(name);

        return userRepository.save(user);
    }


    public String login(LoginRequest request) {
   
        String username = request.getUsername().trim();
        String password = request.getPassword().trim();

        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtProvider.generateToken(user.getUsername());
    }

    public User getUserByUsername(String username) throws Exception {
        return userRepository.findByUsernameIgnoreCase(username.trim())
                .orElseThrow(() -> new Exception("User not found"));
    }
}
