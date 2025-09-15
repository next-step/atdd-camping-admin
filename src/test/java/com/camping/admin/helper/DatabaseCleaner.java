package com.camping.admin.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

/**
 * 각 테스트 시나리오 후 데이터베이스를 깔끔하게 초기화하는 헬퍼 클래스
 *
 * 동작 원리:
 * 1. 모든 테이블의 데이터를 TRUNCATE로 삭제
 * 2. AUTO_INCREMENT 시퀀스를 1로 리셋
 * 3. data.sql을 재실행하여 초기 데이터 복원
 */
@Component
public class DatabaseCleaner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    /**
     * 데이터베이스를 완전히 초기화합니다.
     * 이 메서드는 각 Cucumber 시나리오가 끝난 후 @After Hook에서 호출됩니다.
     */
    public void clean() {
        try {
            System.out.println("🧹 데이터베이스 초기화 시작...");

            // 1. 외래키 제약 조건 비활성화
            jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");

            // 2. 모든 테이블 조회 및 TRUNCATE
            List<String> tableNames = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'",
                String.class
            );

            System.out.println("📋 초기화할 테이블: " + tableNames);

            for (String tableName : tableNames) {
                // 데이터 삭제
                jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
                // ID 시퀀스 리셋
                jdbcTemplate.execute("ALTER TABLE " + tableName + " ALTER COLUMN ID RESTART WITH 1");
            }

            // 3. 외래키 제약 조건 다시 활성화
            jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");

            // 4. data.sql 재실행으로 초기 데이터 복원
            try (Connection connection = dataSource.getConnection()) {
                ScriptUtils.executeSqlScript(connection, new ClassPathResource("data.sql"));
            }

            System.out.println("✅ 데이터베이스 초기화 완료! (" + tableNames.size() + "개 테이블)");

        } catch (Exception e) {
            System.err.println("❌ 데이터베이스 초기화 실패: " + e.getMessage());
            throw new RuntimeException("Database cleaning failed", e);
        }
    }

    /**
     * 현재 데이터베이스 상태를 확인합니다.
     * 디버깅 용도로 사용할 수 있습니다.
     */
    public void printStatus() {
        try {
            List<String> tableNames = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'",
                String.class
            );

            System.out.println("📊 현재 DB 상태:");
            for (String tableName : tableNames) {
                Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM " + tableName, Integer.class
                );
                System.out.println("  - " + tableName + ": " + count + "건");
            }
        } catch (Exception e) {
            System.err.println("DB 상태 확인 실패: " + e.getMessage());
        }
    }
}