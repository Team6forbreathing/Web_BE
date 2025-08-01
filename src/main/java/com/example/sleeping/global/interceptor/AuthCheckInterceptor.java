package com.example.sleeping.global.interceptor;

import com.example.sleeping.auth.application.JwtTokenProvider;
import com.example.sleeping.global.exception.CustomException;
import com.example.sleeping.global.exception.errorCode.UserErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RequiredArgsConstructor
public class AuthCheckInterceptor implements HandlerInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    
    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler
    ) {
        // 헤더의 Authorization 필드에서 토큰 꺼내기
        String token = headerLogic(request.getHeader("Authorization"));
        // 토큰 값이 있으면 로직 수행
        if(!token.isEmpty()) {
            String userId = process(request.getRequestURI(), token);
            request.setAttribute("userId", userId);
            return true;
        }
        
        // 쿠키에 토큰이 들어있는지 확인
        if (request.getCookies() == null) {
            throw CustomException.of(UserErrorCode.NO_COOKIE_DATA);
        }
        
        token = cookieLogic(request.getCookies());
        String userId = process(request.getRequestURI(), token);
        request.setAttribute("userId", userId);
        return true;
    }
    
    @Override
    public void postHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler,
        ModelAndView modelAndView
    ) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
    
    @Override
    public void afterCompletion(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler, Exception ex
    ) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
    
    private String cookieLogic(Cookie[] cookies) {
        String accessToken = null;
        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("accessToken")) {
                accessToken = cookie.getValue();
            }
            if(cookie.getName().equals("refreshToken")) {
                refreshToken = cookie.getValue();
            }
        }
        
        if(accessToken == null || refreshToken == null) {
            throw CustomException.of(UserErrorCode.NO_COOKIE_DATA);
        }
        
        return accessToken;
    }
    
    private String headerLogic(String token){
        if (token == null) {
            return "";
        }
        
        // Bearer 로 시작하는 유효한 토큰인지 확인
        if (!token.startsWith("Bearer ")){
            return "";
        }
        
        // 실제 토큰 내용 추출
        return token.substring(7); // "Bearer " 부분을 제거
    }
    
    private String process(String uri, String token) {
        if(uri.startsWith("/api/admin") && !jwtTokenProvider.tokenRole(token).equals("ADMIN")) {
            throw CustomException.of(UserErrorCode.NO_ADMIN);
        }
        
        if(uri.startsWith("/api/authUser") &&
               (!jwtTokenProvider.tokenRole(token).equals("ADMIN") && !jwtTokenProvider.tokenRole(token).equals("AUTHORIZED_USER"))
        ) {
            throw CustomException.of(UserErrorCode.NO_AUTHORIZED_USER);
        }
        
        return jwtTokenProvider.tokenParsing(token);
    }
}
