package com.example.sleeping.data.presentation.dto;

public record AccMeasurement(
        int id,
        int accX,
        int accY,
        int accZ,
        Long timestamp
) {
}
