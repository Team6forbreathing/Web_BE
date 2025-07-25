package com.example.sleeping.data.application;

import com.example.sleeping.data.persisteent.DataCountRepository;
import com.example.sleeping.global.exception.CustomException;
import com.example.sleeping.global.exception.errorCode.DataErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FileReadService {
    private final Path baseDir = Paths.get("/app/uploads");
    
    private final DataCountRepository dataCountRepository;
    
    // 특정 기간 동안의 데이터 파일의 리스트를 조회
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
    
    // 특정 날짜의 데이터 파일을 조회
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
    
    // 파일 개수 조회
    @Transactional(readOnly = true)
    public Long getFileCount() {
        return dataCountRepository.findById(1L)
                   .orElseThrow().getCount();
    }
}
