package com.example.sleeping.wfdb;

import com.example.sleeping.global.exception.CustomException;
import com.example.sleeping.global.exception.errorCode.DataErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class FlaskClient {
    private final RestTemplate restTemplate;
    
    @Value("${flask.url}")
    private String url;
    
    public void translateToWFDB(Path sendFilePath, Path returnFilePath) {
        FileSystemResource fileSystemResource = new FileSystemResource(sendFilePath);
        
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileSystemResource);
        
        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setAccept(
            List.of(MediaType.APPLICATION_OCTET_STREAM, MediaType.valueOf("application/zip"))
        );
        
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        
        ResponseEntity<byte[]> response = restTemplate.exchange(
            url + "/upload",
            HttpMethod.POST,
            entity,
            byte[].class
        );
        
        if(response.getStatusCode() != HttpStatus.OK) {
            log.info("서버 응답 실패");
            throw CustomException.of(DataErrorCode.FLASK_SERVER_PROBLEM);
        }
        
        byte[] zipBytes = response.getBody();
        if(zipBytes == null) {
            log.info("응답 내용이 없음");
            throw CustomException.of(DataErrorCode.FLASK_SERVER_PROBLEM);
        }
        
        try(FileOutputStream outputStream = new FileOutputStream(String.valueOf(returnFilePath))) {
            outputStream.write(zipBytes);
        } catch (IOException e) {
            log.info("파일 작성 실패");
            e.printStackTrace();
            throw CustomException.of(DataErrorCode.FLASK_SERVER_PROBLEM);
        }
        
        log.info("Flask Client 작동, WFDB 변환 완료");
    }
}
