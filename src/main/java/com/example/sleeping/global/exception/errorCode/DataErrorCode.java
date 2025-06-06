package com.example.sleeping.global.exception.errorCode;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum DataErrorCode implements CustomErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "D001", "Data not found"),
    FLASK_SERVER_PROBLEM(HttpStatus.SERVICE_UNAVAILABLE, "D002", "Flask server problem");

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
