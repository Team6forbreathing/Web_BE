package com.example.sleeping.data.application;

import com.example.sleeping.data.domain.DataCount;
import com.example.sleeping.data.persisteent.DataCountRepository;
import com.example.sleeping.data.presentation.dto.AccMeasurement;
import com.example.sleeping.data.presentation.dto.PpgMeasurement;
import com.example.sleeping.global.exception.CustomException;
import com.example.sleeping.global.exception.errorCode.DataErrorCode;
import com.example.sleeping.wfdb.FlaskClient;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
    private final DataCountRepository dataCountRepository;
    private final FlaskClient flaskClient;

    private final Path baseDir = Paths.get("/app/uploads");

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
    
    // 특정 날짜를 기준으로 정오에서 다음 날 정오사이의 수면을 범위로 검색조건 생성
    private Pair<Instant, Instant> getTimeRange(LocalDate date) {
        ZoneId seoulZone = ZoneId.of("Asia/Seoul");

        ZonedDateTime startZdt = date.atTime(12, 0).atZone(seoulZone);
        ZonedDateTime endZdt = date.plusDays(1).atTime(12, 0).atZone(seoulZone);

        return Pair.of(startZdt.toInstant(), endZdt.toInstant());
    }
    
    // 특정 날짜의 ACC 데이터 조회
    private List<AccMeasurement> queryByOneUnitAcc(LocalDate date, String userId) {
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
    
    // 특정 날짜의 PPG 데이터 조회
    private List<PpgMeasurement> queryByOneUnitPpg(LocalDate date, String userId) {
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
    
    // 하루치 데이터를 읽고 파일화
    public boolean generateFilesForDate(LocalDate date, String userId) throws IOException {
        List<AccMeasurement> accData = queryByOneUnitAcc(date, userId);
        List<PpgMeasurement> ppgData = queryByOneUnitPpg(date, userId);

        if(accData.isEmpty() && ppgData.isEmpty()) {
            return false;
        }
        
        // ACC 데이터가 존재하는 경우 -> dataId에 따라 수면 단위로 나누어 파일화
        if(!accData.isEmpty()) {
            List<AccMeasurement> current = new ArrayList<>();
            int count = 0;

            Iterator<AccMeasurement> iterator = accData.iterator();
            while (iterator.hasNext()) {
                AccMeasurement m = iterator.next();
                // 새로운 수면 단위가 시작된 경우
                if (m.id() == 1 && !current.isEmpty()) {
                    makeFile(date, "accData", userId, current, count++);
                    current = new ArrayList<>();
                }
                current.add(m);
            }
            
            // 끝까지 다 조회한 경우
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
                // 새로운 수면 단위가 시작된 경우
                if (m.id() == 1 && !current.isEmpty()) {
                    makeFile(date, "ppgData", userId, current, count++);
                    current = new ArrayList<>();
                }
                current.add(m);
            }
            
            // 끝까지 다 조회한 경우
            if (!current.isEmpty()) {
                makeFile(date, "ppgData", userId, current, count);
            }
        }

        return true;
    }
    
    // 파일 생성 메서드
    private void makeFile(
            LocalDate date, String dataType, String userId, List<?> data, int count
    ) throws IOException {
        // 최상위 디렉토리 : 날짜
        Path dateDir = baseDir.resolve(date.toString());
        Files.createDirectories(dateDir);
        
        // 그 다음 디렉토리 : 유저 ID
        Path userDir = dateDir.resolve(userId);
        Files.createDirectories(userDir);
        
        // Json 파일, CSV 파일 생성자
        ObjectMapper mapper = new ObjectMapper();
        CsvMapper csvMapper = new CsvMapper();
        
        // Json 파일 생성 (경로 : 날짜/유저ID/파일타입_수면회차.json)
        String fileName = dataType + "_" + count;
        Path jsonFile = userDir.resolve(fileName + ".json");
        mapper.writeValue(jsonFile.toFile(), data);
        
        // CSV 파일 생성 (경로 : 날짜/유저ID/파일타입_수면회차.csv)
        Path csvFile = userDir.resolve(fileName + ".csv");
        CsvSchema schema = csvMapper.schemaFor(data.get(0).getClass()).withHeader();  // 첫 줄에 헤더 포함
        csvMapper.writer(schema).writeValue(csvFile.toFile(), data);
        
        // WFDB 파일 생성 (경로 : 날짜/유저ID/파일타입_수면회차.zip)
        translateFileForm(csvFile, userDir.resolve(fileName + ".zip"));
    }
    
    // 데이터 파일의 리스트를 조회
    public List<List<String>> readDataFileNameList(
            LocalDate startDate, LocalDate endDate, String userId
    ) {
        List<List<String>> result = new ArrayList<>();
        LocalDate date = startDate;
        while(endDate.isAfter(date) || endDate.isEqual(date)) {
            result.add(findFileByUserIdAndDate(userId, date));
            date = date.plusDays(1);
        }

        return result;
    }
    
    // 유저 ID와 날짜를 이용해서 데이터 검색
    public List<String> findFileByUserIdAndDate(String userId, LocalDate date) {
        Path dir = baseDir.resolve(date.toString());
        Path userDir = dir.resolve(userId);

        try (Stream<Path> files = Files.list(userDir)) {
            return files.map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.EMPTY_LIST;
        }
    }
    
    // 파일 다운로드를 위한 경로 전달
    public Resource getFileForDownload(LocalDate date, String userId, String filename) throws IOException {
        Path file = baseDir.resolve(date.toString()).resolve(userId).resolve(filename);
        if (!Files.exists(file)) {
            throw CustomException.of(DataErrorCode.NOT_FOUND);
        }
        return new UrlResource(file.toUri());
    }
    
    // 데이터 개수 집계
    @Transactional
    public void dataCounting() {
        File directory = baseDir.toFile();
        Long count = countFiles(directory);

        if(!dataCountRepository.existsById(1L)) {
            DataCount dataCount = DataCount.from(count);
            dataCountRepository.save(dataCount);
            return;
        }

        DataCount dataCount = dataCountRepository.findById(1L).orElseThrow();
        dataCount.updateCount(count);
    }
    
    // 데이터 개수 세기
    private Long countFiles(File dir) {
        Long count = 0L;
        File[] files = dir.listFiles();

        if (files == null) return 0L;

        for (File file : files) {
            if (file.isFile()) {
                count++;
            } else if (file.isDirectory()) {
                count += countFiles(file); // 재귀 호출로 하위 폴더 탐색
            }
        }

        return count;
    }
    
    // 파일 개수 조회
    @Transactional(readOnly = true)
    public Long getFileCount() {
        return dataCountRepository.findById(1L)
                .orElseThrow().getCount();
    }

    // 파일 업로드
    public void uploadFile(LocalDate date, String userId, MultipartFile multipartFile) throws IOException {
        Path dateDir = baseDir.toAbsolutePath().resolve(date.toString());
        Files.createDirectories(dateDir);

        Path userDir = dateDir.resolve(userId);
        Files.createDirectories(userDir);

        String originalFileName = multipartFile.getOriginalFilename();

        // 저장 경로 설정
        Path filePath = userDir.resolve(originalFileName);

        // 파일 저장
        multipartFile.transferTo(filePath.toFile());
    }
    
    // WFDB 형식 파일 요청하기
    public void translateFileForm(Path sendFilePath, Path returnFilePath) {
        flaskClient.translateToWFDB(sendFilePath, returnFilePath.toAbsolutePath());
    }
}
