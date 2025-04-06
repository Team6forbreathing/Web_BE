package com.example.sleeping.auth.presentation;

import com.example.sleeping.auth.application.AuthService;
import com.example.sleeping.auth.application.JwtTokenProvider;
import com.example.sleeping.auth.token.Token;
import com.example.sleeping.global.dto.Message;
import com.example.sleeping.global.exception.CustomException;
import com.example.sleeping.global.exception.errorCode.AuthErrorCode;
import com.example.sleeping.user.presentation.dto.UserRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    ObjectMapper snakeMapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody String data
    ) throws JsonProcessingException {
        UserRequest userRequest = snakeMapper.readValue(data, UserRequest.class);
        authService.register(userRequest);

        return new ResponseEntity<>(
                Message.of("User registered successfully"),
                HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody String data
    ) throws JsonProcessingException{
        UserRequest userRequest = snakeMapper.readValue(data, UserRequest.class);
        String userName = authService.login(userRequest);

        Token token = jwtTokenProvider.generateToken(userRequest.userId());

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", token.accessToken())
                .httpOnly(true)
                .sameSite("Strict")
                .path("/")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", token.refreshToken())
                .httpOnly(true)
                .sameSite("Strict")
                .path("/")
                .build();

        ResponseCookie userNameCookie = ResponseCookie.from("user_name", userName)
                .httpOnly(false)  // HttpOnly가 아니므로 JavaScript에서 접근 가능
                .sameSite("Strict")
                .path("/")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, userNameCookie.toString());

        return new ResponseEntity<>(
                Message.of("Login Success!"),
                headers, HttpStatus.OK);
    }

    @GetMapping("/check")
    public ResponseEntity<?> accessTokenCheck(@RequestHeader("Authorization") String token) {
        if(token.isEmpty() || token == null) {
            return new ResponseEntity<>("Invalid token format. Expected 'Bearer <token>'.", HttpStatus.BAD_REQUEST);
        }

        if (!token.startsWith("Bearer ")){
            throw CustomException.of(AuthErrorCode.INVALID_BEARER_TOKEN);
        }

        String accessToken = token.substring(7);
        jwtTokenProvider.tokenParsing(accessToken);

        return new ResponseEntity<>(
                Message.of("Access token is valid"),
                HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshTokenRotation(@RequestHeader("Authorization") String token) {
        if(token.isEmpty() || token == null) {
            return new ResponseEntity<>("Invalid token format. Expected 'Bearer <token>'.", HttpStatus.BAD_REQUEST);
        }

        if (!token.startsWith("Bearer ")){
            throw CustomException.of(AuthErrorCode.INVALID_BEARER_TOKEN);
        }

        String refreshToken = token.substring(7);
        String userId = jwtTokenProvider.tokenParsing(refreshToken);

        Token newToken = jwtTokenProvider.generateToken(userId);

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", newToken.accessToken())
                .httpOnly(true)
                .sameSite("Strict")
                .path("/")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", newToken.refreshToken())
                .httpOnly(true)
                .sameSite("Strict")
                .path("/")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return new ResponseEntity<>(
                Message.of("Token refreshed successfully"),
                headers, HttpStatus.OK);
    }
}
