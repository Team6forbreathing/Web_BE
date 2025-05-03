package com.example.sleeping.data.application;

import com.example.sleeping.data.application.dto.DataRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncQueueService {
    private final SensorDataService sensorDataService;
    private final Queue<DataRequest> requestQueue = new LinkedList<>();

    // 큐에 요청을 추가하는 메서드
    public void addRequestToQueue(DataRequest request) {
        synchronized (requestQueue) {
            requestQueue.offer(request);
        }
    }

    @Async
    public void processRequestsFromQueue(int number) {
        while (true) {
            DataRequest request = null;
            synchronized (requestQueue) {
                request = requestQueue.poll();
            }
            if (request != null) {
                // 요청 처리 로직 (예: DB 작업, 외부 API 호출 등)
                sensorDataService.writeAccDataBulk(request.data().accList(), request.userId());
                sensorDataService.writePpgDataBulk(request.data().ppgList(), request.userId());
                log.info("thread-" + number + " : 작업 완료");
            }
        }
    }
}
