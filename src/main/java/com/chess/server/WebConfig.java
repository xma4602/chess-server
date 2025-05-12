package com.chess.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Разрешить доступ ко всем URL
                .allowedOrigins("https://chess-frontend-wpc3.onrender.com:4200") // Разрешите доступ только с этого домена
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Разрешите необходимые методы
                .allowedHeaders("*") // Разрешите все заголовки
                .allowCredentials(true); // Разрешите отправку куки
    }
}