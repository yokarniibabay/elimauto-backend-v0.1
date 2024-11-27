package com.example.elimauto.services;

import com.example.elimauto.models.Role;
import com.example.elimauto.repositories.RoleRepository;
import com.example.elimauto.repositories.UserRepository;
import com.example.elimauto.security.JWTService;
import com.example.elimauto.models.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JWTService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String registerUser(User user) {
        if (userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким номером телефона уже существует");
        }

        validateUserFields(user);

        user.setPassword(passwordEncoder.encode(user.getRawPassword()));
        user.setRawPassword(null);
        user.setEnabled(true);

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Роль ROLE_USER не найдена в базе данных"));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);

        return "Пользователь успешно зарегистрирован!";
    }

    private void validateUserFields(User user) {
        if (user.getPhoneNumber() == null || user.getPhoneNumber().isBlank()) {
            throw new IllegalArgumentException("Номер телефона обязателен");
        }
        if (user.getRawPassword() == null || user.getRawPassword().length() < 8 || user.getRawPassword().length() > 20) {
            throw new IllegalArgumentException("Пароль должен быть длиной от 8 до 20 символов");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
    }

    public String loginUser(String phoneNumber, String password) {
        Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);

        if (userOptional.isEmpty() || !userOptional.get().getEnabled()) {
            throw new IllegalArgumentException("Пользователь не найден или не активирован");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Неверный пароль");
        }

        return jwtService.generateToken(user);
    }
}
