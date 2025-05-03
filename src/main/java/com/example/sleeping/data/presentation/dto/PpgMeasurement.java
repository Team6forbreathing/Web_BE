package com.example.sleeping.data.presentation.dto;

public record PpgMeasurement(
        int id,
        int ppgIR,
        int ppgR,
        int ppgG,
        int statusIR,
        int statusR,
        int statusG,
        Long timestamp
) {
}
