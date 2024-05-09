import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave") // 그 데이터를 zipkin으로 reporting 해주는 구현체
    implementation("io.micrometer:micrometer-tracing:1.2.5") // tracing 가능하게 해주는 기능 sleuth 대체
    implementation("io.micrometer:micrometer-tracing-bridge-brave:1.2.5") // b3 propagation의 java 구현체인 brave와 연결
    implementation("io.micrometer:micrometer-registry-prometheus:1.12.5")

    // kafka 관련 설정
    implementation("org.springframework.kafka:spring-kafka")
    implementation ("com.github.danielwegener:logback-kafka-appender:0.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.6.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
