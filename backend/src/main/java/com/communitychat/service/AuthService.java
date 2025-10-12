package com.communitychat.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.communitychat.model.dto.LoginRequest;
import com.communitychat.model.dto.SignupRequest;
import com.communitychat.model.entity.User;
import com.communitychat.repository.UserRepository;
import com.communitychat.util.JwtUtil;
import com.communitychat.util.PasswordUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User signup(SignupRequest request) throws Exception {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new Exception("Email already registered");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new Exception("Username already taken");
        }

        String hashedPassword = PasswordUtil.hashPassword(request.getPassword());
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

    public String login(LoginRequest request) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (!optionalUser.isPresent()) {
            throw new Exception("User not found");
        }

        User user = optionalUser.get();
        if (!PasswordUtil.verifyPassword(request.getPassword(), user.getPassword())) {
            throw new Exception("Invalid password");
        }

        return JwtUtil.generateToken(user.getId());
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
}
