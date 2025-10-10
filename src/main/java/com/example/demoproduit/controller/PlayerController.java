package com.example.demoproduit.controller;

import com.example.demoproduit.entities.User;
import com.example.demoproduit.services.UserService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@CrossOrigin(origins = "*")
public class PlayerController {

    private final UserService userService;
    public PlayerController(UserService userService) { this.userService = userService; }

    @GetMapping("/online")
    public List<User> getOnlinePlayers() { return userService.getConnectedUsers(); }
}
