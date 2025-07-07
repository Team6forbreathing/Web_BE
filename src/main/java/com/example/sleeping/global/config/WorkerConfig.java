package com.example.sleeping.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class WorkerConfig {
    @Bean
    public ThreadPoolTaskExecutor workerTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4); // 항상 실행되고 있어야 하는 스레드 개수
        executor.setMaxPoolSize(4);  // 최대 생성될 수 있는 스레드 개수
        executor.setQueueCapacity(1000); // 작업 큐의 최대 용량
        executor.setThreadNamePrefix("DataWorkerThread-"); // 스레드 이름 prefix
        executor.initialize();
        return executor;
    }
}
