package com.example.sleeping.global.dto;

public record Message(
        String message
) {
    public static Message of(String message) {
        return new Message(message);
    }
}
