package com.example.sleeping.data.application;

import com.example.sleeping.data.presentation.dto.AccMeasurement;
import com.example.sleeping.data.presentation.dto.PpgMeasurement;
import com.example.sleeping.data.util.DateUtil;
import com.example.sleeping.global.property.InfluxDbProperties;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfluxDbService {
    private final InfluxDbProperties properties;
    private InfluxDBClient client;
    private WriteApiBlocking writeApi;
    
    // InfluxDB를 사용하기 위한 연결 설정
    @PostConstruct
    private void init() {
        client = InfluxDBClientFactory.create(
            properties.url(),
            properties.token().toCharArray(),
            properties.org(),
            properties.bucket()
        );
        writeApi = client.getWriteApiBlocking();
    }
    
    // InfluxDB에 ACC 데이터 삽입
    public void writeAccDataBulk(List<AccMeasurement> accList, String userId) {
        List<Point> points = new ArrayList<>();
        for (AccMeasurement data : accList) {
            Point point = Point.measurement("acc_data")
                              .addTag("userId", userId)
                              .addField("dataId", data.id())
                              .addField("accX", data.accX())
                              .addField("accY", data.accY())
                              .addField("accZ", data.accZ())
                              .time(Instant.ofEpochMilli(data.timestamp()), WritePrecision.MS);
            points.add(point);
        }
        
        writeApi.writePoints(points);
    }
    
    // InfluxDB에 PPG 데이터 삽입
    public void writePpgDataBulk(List<PpgMeasurement> ppgList, String userId) {
        List<Point> points = new ArrayList<>();
        for (PpgMeasurement data : ppgList) {
            Point point = Point.measurement("ppg_data")
                              .addTag("userId", userId)
                              .addField("dataId", data.id())
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
    
    // 특정 날짜의 ACC 데이터 조회
    public List<AccMeasurement> queryByOneUnitAcc(LocalDate date, String userId) {
        Pair<Instant, Instant> timeRange = DateUtil.getTimeRange(date);
        Instant start = timeRange.getFirst();
        Instant end = timeRange.getSecond();
        
        String flux = String.format("""
            from(bucket: "%s")
              |> range(start: %s, stop: %s)
              |> filter(fn: (r) => r._measurement == "acc_data")
              |> filter(fn: (r) => r["userId"] == "%s")
              |> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value")
              |> sort(columns: ["_time"])
        """, properties.bucket(), start, end, userId);
        
        List<AccMeasurement> result = new ArrayList<>();
        
        QueryApi queryApi = client.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux, properties.org());
        
        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                Instant time = record.getTime();
                int accX = ((Number) record.getValueByKey("accX")).intValue();
                int accY = ((Number) record.getValueByKey("accY")).intValue();
                int accZ = ((Number) record.getValueByKey("accZ")).intValue();
                int dataId = ((Number) record.getValueByKey("dataId")).intValue();
                
                result.add(new AccMeasurement(
                    dataId,
                    accX,
                    accY,
                    accZ,
                    time.toEpochMilli()
                ));
            }
        }
        return result;
    }
    
    // 특정 날짜의 PPG 데이터 조회
    public List<PpgMeasurement> queryByOneUnitPpg(LocalDate date, String userId) {
        Pair<Instant, Instant> timeRange = DateUtil.getTimeRange(date);
        Instant start = timeRange.getFirst();
        Instant end = timeRange.getSecond();
        
        String flux = String.format("""
            from(bucket: "%s")
              |> range(start: %s, stop: %s)
              |> filter(fn: (r) => r._measurement == "ppg_data")
              |> filter(fn: (r) => r["userId"] == "%s")
              |> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value")
              |> sort(columns: ["_time"])
        """, properties.bucket(), start, end, userId);
        
        List<PpgMeasurement> result = new ArrayList<>();
        
        QueryApi queryApi = client.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux, properties.org());
        
        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                Instant time = record.getTime();
                int ppgIR = ((Number) record.getValueByKey("ppgIR")).intValue();
                int ppgR = ((Number) record.getValueByKey("ppgR")).intValue();
                int ppgG = ((Number) record.getValueByKey("ppgG")).intValue();
                int statusIR = ((Number) record.getValueByKey("statusIR")).intValue();
                int statusR = ((Number) record.getValueByKey("statusR")).intValue();
                int statusG = ((Number) record.getValueByKey("statusG")).intValue();
                int dataId = ((Number) record.getValueByKey("dataId")).intValue();
                
                result.add(new PpgMeasurement(
                    dataId,
                    ppgIR,
                    ppgR,
                    ppgG,
                    statusIR,
                    statusR,
                    statusG,
                    time.toEpochMilli()
                ));
            }
        }
        return result;
    }
    
    
}
