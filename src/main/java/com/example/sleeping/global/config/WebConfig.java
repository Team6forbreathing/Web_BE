package com.example.sleeping.global.config;

import com.example.sleeping.auth.application.JwtTokenProvider;
import com.example.sleeping.global.interceptor.AdminArgumentResolver;
import com.example.sleeping.global.interceptor.AuthCheckInterceptor;
import com.example.sleeping.global.interceptor.UserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type")
                .exposedHeaders("Custom-Header")
                .allowCredentials(true)
                .maxAge(3600);
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthCheckInterceptor(jwtTokenProvider))
            .addPathPatterns(
                "/api/**",
                "/admin/**"
            )
            .excludePathPatterns(
                "/api/auth/register",
                "/api/auth/login",
                "/api/auth/logout",
                "/admin",
                "/error",
                "/favicon.ico"
            );
    }
}
