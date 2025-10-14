package com.communitychat.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.communitychat.model.entity.User;
import com.communitychat.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(User user) throws Exception {
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (!existingUser.isPresent()) {
            throw new Exception("User not found");
        }

        User updated = existingUser.get();
        updated.setUsername(user.getUsername());
        updated.setEmail(user.getEmail());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            updated.setPassword(user.getPassword());
        }

        return userRepository.save(updated);
    }
    public List<User> searchUsersByUsername(String username) {
        return userRepository.findTop5ByUsernameContainingIgnoreCase(username);
    }
}
