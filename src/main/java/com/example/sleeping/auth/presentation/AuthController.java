package com.example.sleeping.auth.presentation;

import com.example.sleeping.auth.application.AuthService;
import com.example.sleeping.auth.application.CookieUtil;
import com.example.sleeping.auth.application.JwtTokenProvider;
import com.example.sleeping.auth.token.Token;
import com.example.sleeping.global.dto.Message;
import com.example.sleeping.global.exception.CustomException;
import com.example.sleeping.global.exception.errorCode.AuthErrorCode;
import com.example.sleeping.user.application.UserService;
import com.example.sleeping.user.application.command.UserCommand;
import com.example.sleeping.user.presentation.dto.UserRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    ObjectMapper snakeMapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody String data
    ) throws JsonProcessingException {
        UserRequest userRequest = snakeMapper.readValue(data, UserRequest.class);
        authService.register(UserCommand.of(userRequest));

        return new ResponseEntity<>(
                Message.of("User registered successfully"),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody String data
    ) throws JsonProcessingException{
        UserRequest userRequest = snakeMapper.readValue(data, UserRequest.class);
        String role = authService.login(userRequest);

        Token token = jwtTokenProvider.generateToken(userRequest.userId());

        HttpHeaders headers = CookieUtil.cookieSet(token, role);

        return new ResponseEntity<>(
                Message.of(role),
                headers,
                HttpStatus.OK
        );
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
        String userName = userService.readUserData(userId).userName();

        Token newToken = jwtTokenProvider.generateToken(userId);

        HttpHeaders headers = CookieUtil.cookieSet(newToken, userName);

        return new ResponseEntity<>(
                Message.of("Token refreshed successfully"),
                headers,
                HttpStatus.OK
        );
    }
}
