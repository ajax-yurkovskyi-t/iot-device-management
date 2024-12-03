plugins {
    `subproject-conventions`
    kotlin("kapt")
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.mapstruct:mapstruct:1.6.0")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation(project(":internal-api"))
    implementation(project(":core"))
    kapt("org.mapstruct:mapstruct-processor:1.6.0")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.6")
}
