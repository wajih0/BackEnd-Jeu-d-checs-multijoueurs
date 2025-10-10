package com.example.demoproduit.controller;


import com.example.demoproduit.entities.User;
import com.example.demoproduit.services.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    public AuthController(UserService userService) { this.userService = userService; }

    @PostMapping("/register")
    public User register(@RequestBody User user) { return userService.register(user); }

    @PostMapping("/login")
    public User login(@RequestBody User user) { return userService.login(user.getUsername(), user.getPassword()); }
}
