package com.example.sleeping.global.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "influxdb")
public record InfluxDbProperties(
    String bucket,
    String org,
    String token,
    String url
) {}
