package com.example.demoproduit.services;

import com.example.demoproduit.entities.User;
import com.example.demoproduit.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .map(u -> {
                    u.setConnected(true);
                    return userRepository.save(u);
                })
                .orElse(null);
    }

    public List<User> getConnectedUsers() {
        return userRepository.findAll()
                .stream()
                .filter(User::isConnected)
                .toList();
    }
}
