package com.example.elimauto.controllers;

import com.example.elimauto.models.Role;
import com.example.elimauto.models.User;
import com.example.elimauto.repositories.RoleRepository;
import com.example.elimauto.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String phoneNumber, @RequestParam String password) {
        var user = userRepository.findByPhoneNumber(phoneNumber);

        if (user.isEmpty() || !user.get().getEnabled()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Пользователь не найден или не активирован");
        }

        if (!passwordEncoder.matches(password, user.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный пароль");
        }

        return ResponseEntity.ok("Успешный вход");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Пользователь с таким номером телефона уже зарегистрирован.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setEnabled(true);

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Роль ROLE_USER не найдена в базе данных"));

        user.setRoles(Set.of(userRole));

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("Пользователь успешно зарегистрирован");
    }

    @GetMapping("/check-phone/{phoneNumber}")
    public ResponseEntity<Boolean> checkPhoneNumber(@PathVariable String phoneNumber) {
        boolean exists = userRepository.findByPhoneNumber(phoneNumber).isPresent();
        return ResponseEntity.ok(exists);
    }

//    @PostMapping("/register")
//    public String handleRegistration(@ModelAttribute User user, Model model) {
//        // Логика сохранения пользователя с шифрованием пароля
//        return "redirect:/login";
//    }
}
