package com.example.sleeping.global.config;

import com.example.sleeping.auth.application.JwtTokenProvider;
import com.example.sleeping.global.interceptor.AuthCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
                "/auth/**",
                "/user/**",
                "/sensor/**",
                "/authUser"
            )
            .excludePathPatterns(
                "/auth/register",
                "/auth/login",
                "/user/count",
                "/sensor/count",
                "/admin/loginPage",
                "/error",
                "/favicon.ico"
            );
    }
}
