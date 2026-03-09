package com.camping.admin.support;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;

import java.util.List;

public interface DatabaseCleaner {

    void clean();

    /**
     * JPA Metamodel에서 관리되는 모든 엔티티의 테이블 이름을 조회한다.
     *
     * @Table(name = ...) 이 있으면 그 값을, 없으면 클래스명을 snake_case로 변환한 값을 사용한다.
     */
    static List<String> resolveTableNames(EntityManagerFactory emf) {
        return emf.getMetamodel().getEntities().stream()
                .map(EntityType::getJavaType)
                .map(DatabaseCleaner::tableNameOf)
                .toList();
    }

    private static String tableNameOf(Class<?> entityClass) {
        Table table = entityClass.getAnnotation(Table.class);
        if (table != null && !table.name().isBlank()) {
            return table.name();
        }
        // @Table 이 없으면 CamelCase → snake_case 변환 (JPA 기본 규칙)
        return entityClass.getSimpleName()
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
    }
}
