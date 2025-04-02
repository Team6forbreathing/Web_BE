package com.example.sleeping.global.exception.errorCode;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum UserErrorCode implements CustomErrorCode{
    NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "User not found"),
    PASS_NO_NULL(HttpStatus.BAD_REQUEST, "U002", "Current and new passwords are required"),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "U003", "Current password is incorrect");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public HttpStatus httpStatus() {
        return this.httpStatus;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.message;
    }
}