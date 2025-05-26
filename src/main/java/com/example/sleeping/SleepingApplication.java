package com.example.sleeping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SleepingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SleepingApplication.class, args);
    }

}
