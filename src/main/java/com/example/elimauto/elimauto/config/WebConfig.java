
package com.example.elimauto.elimauto.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Разрешаем доступ только с вашего фронтенда (http://127.0.0.1:5500)
        registry.addMapping("/**")
                .allowedOrigins("http://127.0.0.1:5500") // Разрешить доступ с фронта
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}