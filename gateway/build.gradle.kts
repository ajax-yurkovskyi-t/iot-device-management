plugins {
    `spring-conventions`
    kotlin("kapt")
}

dependencies {
    implementation("org.mapstruct:mapstruct:1.6.0")
    implementation(project(":internal-api"))
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.nats:jnats:2.16.14")
    implementation("io.projectreactor:reactor-core:3.6.10")
    implementation(project(":core")) {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-security")
    }
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("berlin.yuna:nats-server-embedded:2.10.22-rc.4")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    kapt("org.mapstruct:mapstruct-processor:1.6.0")
}

