package com.example.sleeping.data.presentation.dto;

public record AccMeasurement(
        int accX,
        int accY,
        int accZ,
        Long timestamp
) {
}
