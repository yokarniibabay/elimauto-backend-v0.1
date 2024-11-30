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
        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        // Пропуск публичных маршрутов
        if ((requestPath.startsWith("/auth") && method.equalsIgnoreCase("POST")) ||
                requestPath.startsWith("/announcement/all") ||
                requestPath.matches("/announcement/\\d+") ||
                requestPath.startsWith("/api/image/")) {
            log.debug("Пропущен путь без проверки токена: {}", requestPath);
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("Заголовок Authorization отсутствует или не соответствует формату Bearer");
            SecurityContextHolder.clearContext();
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            String phoneNumber = jwtService.extractClaims(token).getSubject();
            if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var user = userRepository.findByPhoneNumber(phoneNumber);
                if (user.isPresent() && user.get().isEnabled() && jwtService.isTokenValid(token, phoneNumber)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user.get(),
                            null,
                            user.get().getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Успешно установлена аутентификация для пользователя: {}", phoneNumber);
                } else {
                    log.warn("Недействительный токен или пользователь заблокирован: {}", phoneNumber);
                    SecurityContextHolder.clearContext();
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке токена: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }
}

