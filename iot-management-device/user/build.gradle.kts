plugins {
    `subproject-conventions`
    `grpc-conventions`
    kotlin("kapt")
    `java-test-fixtures`
    jacoco
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.mapstruct:mapstruct:1.6.0")
    implementation("org.springframework.boot:spring-boot-starter-security:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.2")
    implementation("io.mongock:mongock-springboot-v3:5.4.4")
    implementation("io.mongock:mongodb-springdata-v4-driver:5.4.4")
    implementation("com.google.protobuf:protobuf-java:4.28.2")
    implementation(project(":internal-api"))
    implementation(project(":core"))
    implementation(project(":iot-management-device:device"))
    implementation(project(":iot-management-device:role"))
    kapt("org.mapstruct:mapstruct-processor:1.6.0")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("org.testcontainers:testcontainers:1.19.0")
    testImplementation("org.testcontainers:mongodb:1.19.0")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.6")
}
