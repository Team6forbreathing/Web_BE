package com.example.sleeping.data.application;

import com.example.sleeping.data.application.dto.DataRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AsyncQueueService {
    private final InfluxDbService influxDbService;
    private final ThreadPoolTaskExecutor workerTaskExecutor;
    
    public AsyncQueueService(
        InfluxDbService influxDbService,
        @Qualifier("workerTaskExecutor") ThreadPoolTaskExecutor workerTaskExecutor
    ) {
        this.influxDbService = influxDbService;
        this.workerTaskExecutor = workerTaskExecutor;
    }
    
    public void addRequestToQueue(DataRequest request) {
        workerTaskExecutor.submit(() -> {
            influxDbService.writeAccDataBulk(request.data().accList(), request.userId());
            influxDbService.writePpgDataBulk(request.data().ppgList(), request.userId());
            log.info("{} : Task 수행 완료", Thread.currentThread().getName());
        });
    }
}
