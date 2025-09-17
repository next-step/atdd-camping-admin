package com.camping.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/test-api")
@RequiredArgsConstructor
public class TestDataController {

    private final JdbcTemplate jdbcTemplate;

    @PostMapping("/data/init")
    public ResponseEntity<Map<String, String>> initTestData() {
        try {
            // 먼저 데이터 삭제
            cleanupTestData();

            String sql = loadSqlFile("data.sql");
            executeSqlStatements(sql);
            return ResponseEntity.ok(Map.of("message", "데이터 초기화 완료"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "데이터 초기화 실패: " + e.getMessage()));
        }
    }

    @DeleteMapping("/data/cleanup")
    public ResponseEntity<Map<String, String>> cleanupTestData() {
        try {
            // 모든 테이블 데이터 삭제 (외래키 제약 고려한 순서)
            jdbcTemplate.execute("DELETE FROM rental_records");
            jdbcTemplate.execute("DELETE FROM sales_records");
            jdbcTemplate.execute("DELETE FROM reservations");
            jdbcTemplate.execute("DELETE FROM products");
            jdbcTemplate.execute("DELETE FROM campsites");

            return ResponseEntity.ok(Map.of("message", "모든 데이터 삭제 완료"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "데이터 삭제 실패: " + e.getMessage()));
        }
    }

    private String loadSqlFile(String filename) throws Exception {
        ClassPathResource resource = new ClassPathResource(filename);
        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }

    private void executeSqlStatements(String sql) {
        // 주석 제거 및 줄바꿈을 공백으로 변환
        String cleanedSql = sql.replaceAll("--.*?\n", "")  // 한 줄 주석 제거
                               .replaceAll("\n", " ")         // 줄바꿈을 공백으로
                               .replaceAll("\\s+", " ");      // 연속된 공백을 하나로

        String[] statements = cleanedSql.split(";");
        for (String statement : statements) {
            String trimmed = statement.trim();
            if (!trimmed.isEmpty()) {
                System.out.println("Executing SQL: " + trimmed); // 디버깅용 로그
                jdbcTemplate.execute(trimmed);
            }
        }
    }
}
