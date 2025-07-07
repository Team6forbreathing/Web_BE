package com.example.sleeping.data.application;

import com.example.sleeping.data.application.dto.DataRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AsyncQueueService {
    private final SensorDataService sensorDataService;
    private final ThreadPoolTaskExecutor workerTaskExecutor;
    
    public AsyncQueueService(
        SensorDataService sensorDataService,
        @Qualifier("workerTaskExecutor") ThreadPoolTaskExecutor workerTaskExecutor
    ) {
        this.sensorDataService = sensorDataService;
        this.workerTaskExecutor = workerTaskExecutor;
    }
    
    public void addRequestToQueue(DataRequest request) {
        workerTaskExecutor.submit(() -> {
            sensorDataService.writeAccDataBulk(request.data().accList(), request.userId());
            sensorDataService.writePpgDataBulk(request.data().ppgList(), request.userId());
            log.info(Thread.currentThread().getName() + " : Task 수행 완료");
        });
    }
}
