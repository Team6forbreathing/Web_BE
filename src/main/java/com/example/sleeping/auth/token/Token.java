package com.example.sleeping.auth.token;

public record Token(
        String accessToken, String refreshToken
) {
    public static Token of(String accessToken, String refreshToken) {
        return new Token(accessToken, refreshToken);
    }
}
