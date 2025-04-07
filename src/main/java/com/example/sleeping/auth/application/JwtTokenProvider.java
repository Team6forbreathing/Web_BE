package com.example.sleeping.auth.application;

import com.example.sleeping.auth.token.Token;
import com.example.sleeping.global.exception.CustomException;
import com.example.sleeping.global.exception.errorCode.AuthErrorCode;
import com.example.sleeping.global.exception.errorCode.UserErrorCode;
import com.example.sleeping.user.domain.User;
import com.example.sleeping.user.persistent.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final UserRepository userRepository;

    @Value("${secret.key}")
    private String secretKey;
    @Value("${access.token.expiry}")
    private long accessTokenExpired;
    @Value("${refesh.token.expiry}")
    private long refreshTokenExpired;

    private String makeAccessToken(String userId) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(System.currentTimeMillis());

        return Jwts.builder()
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(new Date(nowMillis + accessTokenExpired))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    private String makeRefreshToken(String userId) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(System.currentTimeMillis());

        return Jwts.builder()
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(new Date(nowMillis + refreshTokenExpired))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public Token generateToken(String userId) {
        return Token.of(makeAccessToken(userId), makeRefreshToken(userId));
    }

    public String getClaimFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("userId", String.class);
        } catch(Exception e){
            return "null";
        }
    }

    @Transactional(readOnly = true)
    public String tokenParsing(String token) {
        if(isExpired(token)) {
            throw CustomException.of(AuthErrorCode.EXPIRED_TOKEN);
        }

        String userId = getClaimFromToken(token);
        // 존재하는 userId인지 검사
        if(!userRepository.existsUserByUserId(userId)) {
            throw CustomException.of(AuthErrorCode.INVALID_TOKEN);
        }

        System.out.println("userId = " + userId);
        return userId;
    }

    private boolean isExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true; // 토큰이 만료되었음
        } catch (Exception e) {
            return false; // 기타 예외 발생 (잘못된 토큰 등)
        }
    }

    public String tokenRole(String token) {
        String userId = tokenParsing(token);

        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> CustomException.of(UserErrorCode.NOT_FOUND)
        );

        return user.getRole().name();
    }
}
