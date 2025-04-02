package com.example.sleeping.user.presentation.dto;

public record PassChangeRequest (
        String currentPassword,
        String newPassword
) {
}
