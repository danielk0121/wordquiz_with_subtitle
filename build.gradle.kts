plugins {
    kotlin("jvm") version "1.9.25" // Java 17을 안정적으로 지원하는 코틀린 버전
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "2.7.18"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "dev.danielk"
version = "0.0.1-SNAPSHOT"

java {
    // 소스와 타겟 모두 17로 지정
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    // Java 17 이상에서는 필요할 수 있는 라이브러리
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // spring default
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // sleuth
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth")

    // configuration processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testAnnotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-inline")
}

dependencyManagement {
    imports {
        // Spring Boot 2.7.x와 호환되는 Spring Cloud 버전
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2021.0.9")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        // 코틀린 바이트코드를 자바 17 버전으로 생성
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
