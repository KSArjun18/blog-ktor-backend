val kotlin_version: String by project
val logback_version: String by project
val ktor_version: String = "3.0.0" // Add this line to define ktor_version
val kotlinx_serialization_version = "1.6.0" // Use the correct serialization library version
val kotlinx_datetime_version = "0.4.0" // Ensure this is also up to date

plugins {
    kotlin("jvm") version "2.0.21"
    id("io.ktor.plugin") version "3.0.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor dependencies
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")

    // Authentication and JWT
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")

    // BCrypt for password hashing
    implementation("org.mindrot:jbcrypt:0.4")

    // Serialization support
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_serialization_version") // Use the correct serialization library
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinx_serialization_version") // Core serialization
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")

    // Support for LocalDateTime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinx_datetime_version") // For LocalDateTime support

    // Logback for logging
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // MongoDB KMongo library
    implementation("org.litote.kmongo:kmongo-coroutine:4.9.0")

    // Testing dependencies
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.0.21")
}
