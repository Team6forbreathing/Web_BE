package com.example.sleeping.data.application;

import com.example.sleeping.data.presentation.dto.AccMeasurement;
import com.example.sleeping.data.presentation.dto.PpgMeasurement;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SensorDataService {
    @Value("${influx.bucket}")
    private String bucket;
    @Value("${influx.org}")
    private String org;
    @Value("${influx.token}")
    private String token;
    @Value("${influx.url}")
    private String influxUrl;

    private InfluxDBClient client;
    private WriteApiBlocking writeApi;

    @PostConstruct
    private void init() {
        client = InfluxDBClientFactory.create(influxUrl, token.toCharArray(), org, bucket);
        writeApi = client.getWriteApiBlocking();
    }

    public void writeAccDataBulk(List<AccMeasurement> accList, String userId) {
        List<Point> points = new ArrayList<>();
        for (AccMeasurement data : accList) {
            Point point = Point.measurement("acc_data")
                    .addTag("userId", userId)
                    .addField("accX", data.accX())
                    .addField("accY", data.accY())
                    .addField("accZ", data.accZ())
                    .time(Instant.ofEpochMilli(data.timestamp()), WritePrecision.MS);
            points.add(point);
        }

        writeApi.writePoints(points);
    }

    public void writePpgDataBulk(List<PpgMeasurement> ppgList, String userId) {
        List<Point> points = new ArrayList<>();
        for (PpgMeasurement data : ppgList) {
            Point point = Point.measurement("ppg_data")
                    .addTag("userId", userId)
                    .addField("ppgIR", data.ppgIR())
                    .addField("ppgR", data.ppgR())
                    .addField("ppgG", data.ppgG())
                    .addField("statusIR", data.statusIR())
                    .addField("statusR", data.statusR())
                    .addField("statusG", data.statusG())
                    .time(Instant.ofEpochMilli(data.timestamp()), WritePrecision.MS);
            points.add(point);
        }

        writeApi.writePoints(points);
    }
}
