package com.camping.admin.steps;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class DbCleaner {

    private static JdbcTemplate jdbcTemplate;

    static {
        DataSource dataSource = createDataSource();
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static DataSource createDataSource() {
        var dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:h2:file:./testdb;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setDriverClassName("org.h2.Driver");
        return dataSource;
    }

    public static void clear() {
        var tableNames = jdbcTemplate.queryForList(
            "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'",
            String.class
        );
        // 외래 키 제약 조건 비활성화
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");

        for (String tableName : tableNames) {
            jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
        }

        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }
}
