package com.example.sleeping.data.application.dto;

import com.example.sleeping.data.presentation.dto.SensorData;

public record DataRequest(String userId, SensorData data) {
    public static DataRequest from(String userId, SensorData data) {
        return new DataRequest(userId, data);
    }
}
