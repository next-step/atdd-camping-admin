package com.camping.admin.support;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DELETE + 시퀀스 리셋 기반 DB 초기화.
 * FK 제약을 일시적으로 비활성화한 후 모든 테이블을 삭제하고,
 * 각 테이블의 ID 시퀀스를 1로 초기화한다.
 * 시나리오 간 ID 예측 가능성이 필요한 테스트에 적합하다.
 */
@Component
@Qualifier("deleteAndReset")
public class DeleteAndResetDatabaseCleaner implements DatabaseCleaner {

    private final JdbcTemplate jdbcTemplate;
    private final List<String> tableNames;

    public DeleteAndResetDatabaseCleaner(JdbcTemplate jdbcTemplate, EntityManagerFactory emf) {
        this.jdbcTemplate = jdbcTemplate;
        this.tableNames = DatabaseCleaner.resolveTableNames(emf);
    }

    @Override
    public void clean() {
        // 동적으로 조회한 테이블은 순서가 보장되지 않으므로 FK 제약을 일시 비활성화
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        tableNames.forEach(table -> jdbcTemplate.execute("DELETE FROM " + table));
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");

        tableNames.forEach(table ->
                jdbcTemplate.execute("ALTER TABLE " + table + " ALTER COLUMN id RESTART WITH 1"));
    }
}
