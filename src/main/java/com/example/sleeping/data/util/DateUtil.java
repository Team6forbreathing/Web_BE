package com.example.sleeping.data.util;

import org.springframework.data.util.Pair;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateUtil {
    // 특정 날짜를 기준으로 정오에서 다음 날 정오사이의 수면을 범위로 검색조건 생성
    public static Pair<Instant, Instant> getTimeRange(LocalDate date) {
        ZoneId seoulZone = ZoneId.of("Asia/Seoul");
        
        ZonedDateTime startZdt = date.atTime(12, 0).atZone(seoulZone);
        ZonedDateTime endZdt = date.plusDays(1).atTime(12, 0).atZone(seoulZone);
        
        return Pair.of(startZdt.toInstant(), endZdt.toInstant());
    }
}
