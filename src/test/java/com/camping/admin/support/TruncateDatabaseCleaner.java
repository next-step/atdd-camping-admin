package com.camping.admin.support;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TRUNCATE 기반 DB 초기화.
 * FK 제약을 일시적으로 비활성화한 후 모든 테이블을 잘라낸다.
 * 시퀀스(AUTO_INCREMENT)는 리셋되지 않는다.
 */
@Component
@Qualifier("truncate")
public class TruncateDatabaseCleaner implements DatabaseCleaner {

    private final JdbcTemplate jdbcTemplate;
    private final List<String> tableNames;

    public TruncateDatabaseCleaner(JdbcTemplate jdbcTemplate, EntityManagerFactory emf) {
        this.jdbcTemplate = jdbcTemplate;
        this.tableNames = DatabaseCleaner.resolveTableNames(emf);
    }

    @Override
    public void clean() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        tableNames.forEach(table -> jdbcTemplate.execute("TRUNCATE TABLE " + table));
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }
}
