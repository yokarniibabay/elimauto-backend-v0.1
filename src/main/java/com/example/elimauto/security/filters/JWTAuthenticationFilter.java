package com.example.elimauto.security.filters;

import com.example.elimauto.repositories.UserRepository;
import com.example.elimauto.security.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserRepository userRepository;

    public JWTAuthenticationFilter(JWTService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain)
            throws IOException, ServletException {
        // Логируем получение заголовка Authorization
        String authHeader = request.getHeader("Authorization");
        log.info("Получен заголовок Authorization: {}", authHeader);

        // Проверяем наличие и формат заголовка
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // Извлекаем номер телефона из токена
                String phoneNumber = jwtService.extractClaims(token).getSubject();
                log.info("Извлечён номер телефона из токена: {}", phoneNumber);

                // Проверяем, есть ли аутентификация в SecurityContext
                if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    var userOptional = userRepository.findByPhoneNumber(phoneNumber);
                    if (userOptional.isPresent()) {
                        var user = userOptional.get();

                        // Проверяем валидность токена
                        if (jwtService.isTokenValid(token, phoneNumber)) {
                            log.info("Токен действителен. Устанавливаем аутентификацию для пользователя: {}", phoneNumber);

                            // Создаём объект аутентификации
                            UsernamePasswordAuthenticationToken authToken =
                                    new UsernamePasswordAuthenticationToken(
                                            user,
                                            null,
                                            user.getAuthorities()
                                    );

                            // Устанавливаем дополнительные детали (например, IP-адрес запроса)
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            // Устанавливаем аутентификацию в SecurityContext
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        } else {
                            log.warn("Токен недействителен для пользователя: {}", phoneNumber);
                        }
                    } else {
                        log.warn("Пользователь с номером телефона {} не найден в базе данных", phoneNumber);
                    }
                }
            } catch (Exception e) {
                log.error("Ошибка при обработке токена: {}", e.getMessage(), e);
            }
        } else {
            log.info("Заголовок Authorization отсутствует или не соответствует формату Bearer");
        }

        // Продолжаем цепочку фильтров
        chain.doFilter(request, response);
    }
}

