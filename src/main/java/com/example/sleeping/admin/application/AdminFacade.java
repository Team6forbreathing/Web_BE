package com.example.sleeping.admin.application;

import com.example.sleeping.global.scheduler.DataCountingScheduler;
import com.example.sleeping.global.scheduler.DataFileScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;

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

    public void launchDataFileScheduler() throws IOException {
        boolean state = dataFileScheduler.getStatus();

        dataFileScheduler.turnOn();
        for (int i = 0; i < 7; i++) {
            dataFileScheduler.scheduledWork(LocalDate.now().plusDays(i));
        }

        if(!state) {
            dataCountingScheduler.turnOff();
        }
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

    public void launchDataCountingScheduler() {
        boolean state = dataCountingScheduler.getStatus();

        dataCountingScheduler.turnOn();
        dataCountingScheduler.scheduledWork();

        if(!state) {
            dataCountingScheduler.turnOff();
        }
    }
}
