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

        if ((requestPath.startsWith("/auth") && method.equalsIgnoreCase("POST")) ||
                requestPath.startsWith("/announcement/all") ||
                requestPath.matches("/announcement/\\d+") ||
                requestPath.startsWith("/api/image/")) {
            chain.doFilter(request, response);
            return;
        }

        // Получаем заголовок Authorization
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("Заголовок Authorization отсутствует или не соответствует формату Bearer");
            chain.doFilter(request, response); // Продолжаем обработку без аутентификации
            return;
        }

        String token = authHeader.substring(7);
        String phoneNumber = jwtService.extractClaims(token).getSubject();

        if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var user = userRepository.findByPhoneNumber(phoneNumber);
            if (user.isPresent() && jwtService.isTokenValid(token, phoneNumber)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user.get(),
                        null,
                        user.get().getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        chain.doFilter(request, response);
    }
}

