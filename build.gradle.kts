plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
    id("org.unbroken-dome.test-sets") version "4.1.0"
}

group = "com.camping"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

val cucumberVersion = "7.14.0"
val restAssuredVersion = "5.3.2"
val jjwtVersion = "0.11.5"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // Database
    runtimeOnly("com.h2database:h2")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Test Dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // JUnit Platform Suite for Cucumber
    testImplementation("org.junit.platform:junit-platform-suite:1.10.0")

    // Cucumber
    testImplementation("io.cucumber:cucumber-java:$cucumberVersion")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:$cucumberVersion")

    // RestAssured
    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
    testImplementation("io.rest-assured:spring-mock-mvc:$restAssuredVersion")

    // JWT (jjwt)
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")
}

testSets {
    create("isolatedCucumber") {
        dirName = "test"
    }
}


// 기본 테스트 태스크
tasks.named<Test>("test") {
    useJUnitPlatform()
}

// 격리된 Cucumber 테스트 태스크
tasks.register<Test>("isolatedCucumberTest") {
    group = "verification"
    description = "격리된 Cucumber 테스트 실행 - 각 시나리오마다 @After Hook으로 DB 자동 초기화"

    useJUnitPlatform()
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath

    // 각 시나리오마다 새 JVM으로 완전 격리
    forkEvery = 1

    // JVM 옵션 최적화
    jvmArgs = listOf(
        "-XX:TieredStopAtLevel=1",  // C1 컴파일러만 사용
        "-XX:+UseParallelGC"        // 병렬 GC 사용
    )

    // Spring 설정 - data.sql 자동 재실행 보장
    systemProperty("spring.jpa.hibernate.ddl-auto", "create-drop")
    systemProperty("spring.sql.init.mode", "always")

    // Cucumber 실행 설정
    systemProperty("cucumber.junit-platform.naming-strategy", "long")

    doFirst {
        println("격리된 Cucumber 테스트 시작 - 각 시나리오마다 새로운 JVM으로 실행")
        println("data.sql이 각 시나리오마다 자동으로 재실행됩니다")
    }
}

// cucumberTest 태스크도 추가 (기존 방식 호환성 유지)
tasks.register<Test>("cucumberTest") {
    group = "verification"
    description = "기존 방식 Cucumber 테스트 - @After Hook으로 DB 자동 초기화"

    useJUnitPlatform()
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath

    systemProperty("cucumber.junit-platform.naming-strategy", "long")
}