package com.camping.admin.steps;

import io.cucumber.java.Before;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

public class Hooks {

    private final EntityManager em;
    private final int port;

    public Hooks(EntityManager em, @Value("${local.server.port}") int port) {
        this.em = em;
        this.port = port;
    }

    @Before
    @Transactional
    public void setUp() {
        cleanUp();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    private void cleanUp() {
        em.flush();
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        var tableNames = em.getMetamodel().getEntities().stream()
            .map(this::getTableName)
            .toList();

        for (var tableName : tableNames) {
            em.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        }

        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    private String getTableName(EntityType<?> entity) {
        var tableAnnotation = entity.getJavaType().getAnnotation(Table.class);
        if (tableAnnotation != null && !tableAnnotation.name().isEmpty()) {
            return tableAnnotation.name();
        }
        return toSnakeCase(entity.getName());
    }

    private String toSnakeCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}
