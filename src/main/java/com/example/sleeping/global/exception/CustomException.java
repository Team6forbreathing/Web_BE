package com.example.sleeping.global.exception;

import com.example.sleeping.global.exception.errorCode.CustomErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    public CustomException(HttpStatus httpStatus, String errorCode, String errorMessage) {
        super(errorMessage);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static CustomException of(CustomErrorCode customErrorCode) {
        return new CustomException(
            customErrorCode.httpStatus(),
            customErrorCode.code(),
            customErrorCode.message()
        );
    }
}
