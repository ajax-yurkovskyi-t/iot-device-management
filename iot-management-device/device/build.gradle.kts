plugins {
    `subproject-conventions`
    `grpc-conventions`
    kotlin("kapt")
    `java-test-fixtures`
    jacoco
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.mapstruct:mapstruct:1.6.0")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("com.google.protobuf:protobuf-java:4.28.2")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("io.projectreactor.kafka:reactor-kafka")
    implementation(project(":internal-api"))
    implementation(project(":core"))
    implementation("systems.ajax:nats-spring-boot-starter:4.1.0.186.MASTER-SNAPSHOT")
    implementation("systems.ajax:kafka-spring-boot-starter:3.0.3.170.MASTER-SNAPSHOT")
    kapt("org.mapstruct:mapstruct-processor:1.6.0")
    testImplementation("systems.ajax:kafka-mock:3.0.3.170.MASTER-SNAPSHOT")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("org.testcontainers:testcontainers:1.19.0")
    testImplementation("org.testcontainers:mongodb:1.19.0")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.6")
}
