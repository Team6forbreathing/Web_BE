package com.example.sleeping.global.exception;

import com.example.sleeping.global.dto.Message;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(
            HttpServletRequest request,
            CustomException exception
    ) {
        log.error(exception.getErrorCode() + " : " + exception.getMessage(), exception);
        return new ResponseEntity<>(
                Message.of(exception.getErrorMessage()),
                exception.getHttpStatus()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(
            HttpServletRequest request,
            Exception exception
    ) {
        log.error("E999" + " : " + exception.getMessage(), exception);
        return new ResponseEntity<>(
                Message.of("Unknown Error Occurred!, pls check your server status"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
