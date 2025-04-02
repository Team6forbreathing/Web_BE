package com.example.sleeping.global.resolver;

import com.example.sleeping.auth.application.JwtTokenProvider;
import com.example.sleeping.global.annotation.LoginUser;
import com.example.sleeping.global.exception.CustomException;
import com.example.sleeping.global.exception.errorCode.AuthErrorCode;
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
        if (token == null) {
            throw CustomException.of(AuthErrorCode.NO_ACCESS_TOKEN);
        }
        // Bearer 로 시작하는 유효한 토큰인지 확인
        if (!token.startsWith("Bearer ")){
            throw CustomException.of(AuthErrorCode.INVALID_BEARER_TOKEN);
        }

        // 실제 토큰 내용 추출
        token = token.substring(7); // "Bearer " 부분을 제거

        // 액세스 동작
        return jwtTokenProvider.tokenParsing(token);
    }
}
