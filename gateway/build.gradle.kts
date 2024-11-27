plugins {
    `spring-conventions`
    `grpc-conventions`
    kotlin("kapt")
}

dependencies {
    implementation(project(":internal-api"))
    implementation(project(":core")) {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-security")
    }
    implementation(project(":grpc-api"))
    implementation("org.mapstruct:mapstruct:1.6.0")
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("systems.ajax:nats-spring-boot-starter:4.1.0.186.MASTER-SNAPSHOT")
    implementation("io.projectreactor:reactor-core:3.6.10")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("net.devh:grpc-server-spring-boot-starter:3.1.0.RELEASE")
    implementation("net.devh:grpc-spring-boot-starter:3.1.0.RELEASE")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    kapt("org.mapstruct:mapstruct-processor:1.6.0")
}
