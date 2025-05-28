package com.example.sleeping.admin.application;

import com.example.sleeping.global.scheduler.DataCountingScheduler;
import com.example.sleeping.global.scheduler.DataFileScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminFacade {
    private final DataFileScheduler dataFileScheduler;
    private final DataCountingScheduler dataCountingScheduler;

    public boolean getDataFileSchedulerStatus() {
        return dataFileScheduler.getStatus();
    }

    public void changeDataFileScheduler() {
        if(dataFileScheduler.getStatus()) {
            dataFileScheduler.turnOff();
            return;
        }

        dataFileScheduler.turnOn();
    }

    public boolean getDataCountingSchedulerStatus() {
        return dataCountingScheduler.getStatus();
    }

    public void changeDataCountingScheduler() {
        if(dataCountingScheduler.getStatus()) {
            dataCountingScheduler.turnOff();
            return;
        }

        dataCountingScheduler.turnOn();
    }
}
