package com.example.sleeping.global.exception.errorCode;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum AuthErrorCode implements CustomErrorCode{
    DUPLICATION(HttpStatus.BAD_REQUEST, "A001", "User ID already exists"),
    WRONG_PW(HttpStatus.BAD_REQUEST, "A002", "Invalid password"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "A003", "User not found"),
    NO_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "A004", "Access token is missing"),
    INVALID_BEARER_TOKEN(HttpStatus.BAD_REQUEST, "A005", "Invalid token format. Expected 'Bearer <token>'."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "A006", "Invalid access token"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A007", "Token has expired"),
    INVALID_DATA_FIELD(HttpStatus.BAD_REQUEST, "A008", "user_id, user_pw, and user_name are required"),
    USER_NAME_SPACE(HttpStatus.BAD_REQUEST, "A009", "user name must be no space");

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
