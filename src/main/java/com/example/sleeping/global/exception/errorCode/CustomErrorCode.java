package com.example.sleeping.global.exception.errorCode;

import org.springframework.http.HttpStatus;

public interface CustomErrorCode {
    HttpStatus httpStatus();

    String code();

    String message();
}
