package com.example.sleeping.global.resolver;

import com.example.sleeping.auth.application.JwtTokenProvider;
import com.example.sleeping.global.annotation.LoginUser;
import com.example.sleeping.global.exception.CustomException;
import com.example.sleeping.global.exception.errorCode.AuthErrorCode;
import com.example.sleeping.global.exception.errorCode.UserErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        // 헤더가 Authorization 필드를 가지는지 확인
        String token = webRequest.getHeader("Authorization");
        if (token != null) {
            return headerLogic(token);
        }

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null || request.getCookies() == null) {
            throw CustomException.of(UserErrorCode.NO_COOKIE_DATA);
        }

        return cookieLogic(request.getCookies());
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

        return jwtTokenProvider.tokenParsing(accessToken);
    }

    private String headerLogic(String token){
        // Bearer 로 시작하는 유효한 토큰인지 확인
        if (!token.startsWith("Bearer ")){
            throw CustomException.of(AuthErrorCode.INVALID_BEARER_TOKEN);
        }

        // 실제 토큰 내용 추출
        token = token.substring(7); // "Bearer " 부분을 제거

        // UserId 반환
        return jwtTokenProvider.tokenParsing(token);
    }
}
