package com.example.elimauto.controllers;

import com.example.elimauto.models.User;
import com.example.elimauto.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> requestBody) {
        try {
            String phoneNumber = requestBody.get("phoneNumber");
            String password = requestBody.get("password");
            String token = userService.loginUser(phoneNumber, password);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            String message = userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

//    @GetMapping("/check-phone/{phoneNumber}")
//    public ResponseEntity<Boolean> checkPhoneNumber(@PathVariable String phoneNumber) {
//        boolean exists = userRepository.findByPhoneNumber(phoneNumber).isPresent();
//        return ResponseEntity.ok(exists);
//    }

//    @PostMapping("/register")
//    public String handleRegistration(@ModelAttribute User user, Model model) {
//        // Логика сохранения пользователя с шифрованием пароля
//        return "redirect:/login";
//    }
}
