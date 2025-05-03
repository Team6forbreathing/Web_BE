package com.example.sleeping.global.resolver;

import com.example.sleeping.auth.application.JwtTokenProvider;
import com.example.sleeping.auth.token.Token;
import com.example.sleeping.global.annotation.AdminUser;
import com.example.sleeping.global.exception.CustomException;
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
public class AdminArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AdminUser.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null || request.getCookies() == null) {
            throw CustomException.of(UserErrorCode.NO_COOKIE_DATA);
        }

        String accessToken = null;
        String refreshToken = null;
        for (Cookie cookie : request.getCookies()) {
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

        String role = jwtTokenProvider.tokenRole(accessToken);

        if(!role.equals("ADMIN")) {
            throw CustomException.of(UserErrorCode.NO_ADMIN);
        }

        return jwtTokenProvider.tokenParsing(accessToken);
    }
}
