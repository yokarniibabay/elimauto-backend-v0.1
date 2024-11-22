package com.example.elimauto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {

    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Настройка авторизации
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Доступ только для ADMIN
                        .requestMatchers("/moderator/**").hasRole("MODERATOR") // Доступ для MODERATOR
                        .anyRequest().authenticated() // Все остальные запросы требуют авторизации
                )
                // Настройка аутентификации
                .formLogin(form -> form
                        .loginPage("/login") // Своя страница логина (опционально)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/") // Перенаправление после выхода
                        .permitAll()
                )
                // CSRF: рекомендуется оставить включенным, но можно отключить для REST API
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
