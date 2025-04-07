package com.example.sleeping.data.presentation.dto;

import java.util.List;

public record SensorData(
        List<AccMeasurement> accList,
        List<PpgMeasurement> ppgList
) {
}
