package com.example.sleeping.data.application;

import com.example.sleeping.data.domain.DataCount;
import com.example.sleeping.data.persisteent.DataCountRepository;
import com.example.sleeping.data.presentation.dto.AccMeasurement;
import com.example.sleeping.data.presentation.dto.PpgMeasurement;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileCudService {
    private final Path baseDir = Paths.get("/app/uploads");
    
    private final DataCountRepository dataCountRepository;
    
    // 특정 date의 InfluxDB 내용을 파일화
    public boolean generateFilesForDate(
        List<AccMeasurement> accData, List<PpgMeasurement> ppgData, LocalDate date, String userId
    ) throws IOException {
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
    
    // 파일 생성 (json, csv)
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
}
