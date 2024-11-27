plugins {
    id("delta-coverage-conventions")
    id("kotlin-conventions")
}

allprojects {
    group = "сom.example"
    version = "0.0.1-SNAPSHOT"
    repositories {
        mavenCentral()

        maven {
            credentials(AwsCredentials::class.java) {
                accessKey = extra["AWS_ACCESS_KEY_ID"].toString()
                secretKey = extra["AWS_SECRET_ACCESS_KEY"].toString()
            }
            url = uri(extra["repository"].toString())
        }
    }
}
