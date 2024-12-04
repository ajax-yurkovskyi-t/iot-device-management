plugins {
    `subproject-conventions`
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("io.mongock:mongock-springboot-v3:5.4.4")
    implementation("io.mongock:mongodb-springdata-v4-driver:5.4.4")
    implementation(project(":iot-management-device:role"))
    implementation(project(":iot-management-device:user"))
}
