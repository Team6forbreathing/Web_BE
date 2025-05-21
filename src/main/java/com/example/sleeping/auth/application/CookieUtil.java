package com.example.sleeping.auth.application;

import com.example.sleeping.auth.token.Token;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CookieUtil {
    public static HttpHeaders cookieSet(Token token, String userName) {
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", token.accessToken())
                .httpOnly(false)
                .sameSite("Strict")
                .path("/")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", token.refreshToken())
                .httpOnly(false)
                .sameSite("Strict")
                .path("/")
                .build();

        String encodedUserName = Base64.getEncoder().encodeToString(userName.getBytes(StandardCharsets.UTF_8));
        ResponseCookie userNameCookie = ResponseCookie.from("user_name", encodedUserName)
                .httpOnly(false)  // HttpOnly가 아니므로 JavaScript에서 접근 가능
                .sameSite("Strict")
                .path("/")
                .build();

        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add(org.springframework.http.HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        headers.add(org.springframework.http.HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        headers.add(org.springframework.http.HttpHeaders.SET_COOKIE, userNameCookie.toString());

        return headers;
    }
}
