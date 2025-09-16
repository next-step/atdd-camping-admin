package com.camping.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

@RestController
public class DatabaseAdminController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/admin/database/reset")
    public ResponseEntity<Map<String, Object>> resetDatabase() {
        try {
            // 1. 외래키 제약 조건 비활성화
            jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");

            // 2. 모든 테이블 조회 및 TRUNCATE
            var tableNames = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'",
                String.class
            );

            for (String tableName : tableNames) {
                jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
                jdbcTemplate.execute("ALTER TABLE " + tableName + " ALTER COLUMN ID RESTART WITH 1");
            }

            // 3. 외래키 제약 조건 다시 활성화
            jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");

            // 4. data.sql 재실행
            try (Connection connection = dataSource.getConnection()) {
                ScriptUtils.executeSqlScript(connection, new ClassPathResource("data.sql"));
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "데이터베이스가 성공적으로 초기화되었습니다.",
                "resetTables", tableNames.size(),
                "tableNames", tableNames
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "데이터베이스 초기화 실패: " + e.getMessage(),
                "error", e.getClass().getSimpleName()
            ));
        }
    }

    // JWT 인증이 필요 없는 public health check 엔드포인트
    @GetMapping("/db-health")
    public ResponseEntity<Map<String, Object>> publicHealthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "database", "PUBLIC_ENDPOINT",
            "timestamp", System.currentTimeMillis()
        ));
    }

    @GetMapping("/admin/database/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            // 간단한 DB 연결 테스트
            var count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM campsites", Integer.class);

            return ResponseEntity.ok(Map.of(
                "status", "UP",
                "database", "CONNECTED",
                "campsiteCount", count,
                "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "DOWN",
                "database", "ERROR",
                "error", e.getMessage()
            ));
        }
    }
}