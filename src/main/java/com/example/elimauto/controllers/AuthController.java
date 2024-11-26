package com.example.elimauto.controllers;

import com.example.elimauto.models.Role;
import com.example.elimauto.models.User;
import com.example.elimauto.repositories.RoleRepository;
import com.example.elimauto.repositories.UserRepository;
import com.example.elimauto.security.JWTService;
import com.example.elimauto.services.PhoneNumberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final PhoneNumberService phoneNumberService;

    @Autowired
    public AuthController(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          JWTService jwtService,
                          PhoneNumberService phoneNumberService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.phoneNumberService = phoneNumberService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> requestBody) {
        String phoneNumber = requestBody.get("phoneNumber");
        String password = requestBody.get("password");
        var user = userRepository.findByPhoneNumber(phoneNumber);

        if (user.isEmpty() || !user.get().getEnabled()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Пользователь не найден или не активирован");
        }

        if (!passwordEncoder.matches(password, user.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный пароль");
        }
        String token = jwtService.generateToken(user.get());

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody User user, BindingResult result) {
        String normalizedPhoneNumber = phoneNumberService.normalizePhoneNumber(user.getPhoneNumber());
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(result.getAllErrors().get(0).getDefaultMessage());
        }

        if (userRepository.findByPhoneNumber(normalizedPhoneNumber).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Пользователь с таким номером телефона уже зарегистрирован.");
        }

        user.setPhoneNumber(normalizedPhoneNumber);
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
