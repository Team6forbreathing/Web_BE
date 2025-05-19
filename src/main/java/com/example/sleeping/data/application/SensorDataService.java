package com.example.sleeping.data.application;

import com.example.sleeping.data.presentation.dto.AccMeasurement;
import com.example.sleeping.data.presentation.dto.PpgMeasurement;
import com.example.sleeping.global.exception.CustomException;
import com.example.sleeping.global.exception.errorCode.DataErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.influxdb.client.*;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SensorDataService {
    @Value("${influxdb.bucket}")
    private String bucket;
    @Value("${influxdb.org}")
    private String org;
    @Value("${influxdb.token}")
    private String token;
    @Value("${influxdb.url}")
    private String influxUrl;

    private InfluxDBClient client;
    private WriteApiBlocking writeApi;

    private final Path baseDir = Paths.get("data-storage");

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
                    .addField("dataId", data.id())
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

    private Pair<Instant, Instant> getTimeRange(LocalDate date) {
        ZoneId seoulZone = ZoneId.of("Asia/Seoul");

        ZonedDateTime startZdt = date.atTime(12, 0).atZone(seoulZone);
        ZonedDateTime endZdt = date.plusDays(1).atTime(12, 0).atZone(seoulZone);

        return Pair.of(startZdt.toInstant(), endZdt.toInstant());
    }

    public List<AccMeasurement> queryByOneUnitAcc(LocalDate date, String userId) {
        Pair<Instant, Instant> timeRange = getTimeRange(date);
        Instant start = timeRange.getFirst();
        Instant end = timeRange.getSecond();

        String flux = String.format("""
            from(bucket: "%s")
              |> range(start: %s, stop: %s)
              |> filter(fn: (r) => r._measurement == "acc_data")
              |> filter(fn: (r) => r["userId"] == "%s")
              |> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value")
              |> sort(columns: ["_time"])
        """, bucket, start, end, userId);

        List<AccMeasurement> result = new ArrayList<>();

        QueryApi queryApi = client.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux, org);

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

    public List<PpgMeasurement> queryByOneUnitPpg(LocalDate date, String userId) {
        Pair<Instant, Instant> timeRange = getTimeRange(date);
        Instant start = timeRange.getFirst();
        Instant end = timeRange.getSecond();

        String flux = String.format("""
            from(bucket: "%s")
              |> range(start: %s, stop: %s)
              |> filter(fn: (r) => r._measurement == "ppg_data")
              |> filter(fn: (r) => r["userId"] == "%s")
              |> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value")
              |> sort(columns: ["_time"])
        """, bucket, start, end, userId);

        List<PpgMeasurement> result = new ArrayList<>();

        QueryApi queryApi = client.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux, org);

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

    public void generateFilesForDate(LocalDate date, String userId) throws IOException {
        List<AccMeasurement> accData = queryByOneUnitAcc(date, userId);
        List<PpgMeasurement> ppgData = queryByOneUnitPpg(date, userId);

        if(accData.isEmpty() && ppgData.isEmpty()) {
            return;
        }

        if(!accData.isEmpty()) {
            List<AccMeasurement> current = new ArrayList<>();
            int count = 0;

            Iterator<AccMeasurement> iterator = accData.iterator();
            while (iterator.hasNext()) {
                AccMeasurement m = iterator.next();
                if (m.id() == 1 && !current.isEmpty()) {
                    makeFile(date, "accData", userId, current, count++);
                    current = new ArrayList<>();
                }
                current.add(m);
            }

            if (!current.isEmpty()) {
                makeFile(date, "accData", userId, current, count);
            }
        }
        if(!ppgData.isEmpty()) {
            List<PpgMeasurement> current = new ArrayList<>();
            int count = 0;

            Iterator<PpgMeasurement> iterator = ppgData.iterator();
            while (iterator.hasNext()) {
                PpgMeasurement m = iterator.next();
                if (m.id() == 1 && !current.isEmpty()) {
                    makeFile(date, "ppgData", userId, current, count++);
                    current = new ArrayList<>();
                }
                current.add(m);
            }

            if (!current.isEmpty()) {
                makeFile(date, "ppgData", userId, current, count);
            }
        }
    }

    private void makeFile(
            LocalDate date, String dataType, String userId, List<?> data, int count
    ) throws IOException {
        Path dateDir = baseDir.resolve(date.toString());
        Files.createDirectories(dateDir);

        Path userDir = dateDir.resolve(userId);
        Files.createDirectories(userDir);

        ObjectMapper mapper = new ObjectMapper();
        CsvMapper csvMapper = new CsvMapper();

        String fileName = dataType + "_" + count;
        Path jsonFile = userDir.resolve(fileName + ".json");
        mapper.writeValue(jsonFile.toFile(), data);

        Path csvFile = userDir.resolve(fileName + ".csv");
        CsvSchema schema = csvMapper.schemaFor(data.get(0).getClass()).withHeader();  // 첫 줄에 헤더 포함
        csvMapper.writer(schema).writeValue(csvFile.toFile(), data);
    }

    public List<List<String>> readDataFileNameList(
            LocalDate startDate, LocalDate endDate, String userId
    ) throws IOException {
        List<List<String>> result = new ArrayList<>();
        LocalDate date = startDate;
        while(endDate.isAfter(date) || endDate.isEqual(date)) {
            Path dir = baseDir.resolve(date.toString());
            Path userDir = dir.resolve(userId);

            try (Stream<Path> files = Files.list(userDir)) {
                result.add(files.map(Path::getFileName)
                        .map(Path::toString)
                        .collect(Collectors.toList()));
            } catch (NoSuchFileException e) {
                result.add(Collections.EMPTY_LIST);
            }

            date = date.plusDays(1);
        }

        return result;
    }

    public Resource getFileForDownload(LocalDate date, String userId, String filename) throws IOException {
        Path file = baseDir.resolve(date.toString()).resolve(userId).resolve(filename);
        if (!Files.exists(file)) {
            throw CustomException.of(DataErrorCode.NOT_FOUND);
        }
        return new UrlResource(file.toUri());
    }
}
