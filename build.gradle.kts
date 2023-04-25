import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion: String = "2.3.0"
val kotlinVersion: String = "1.7.21"
val logbackVersion: String = "1.4.6"

plugins {
    kotlin("jvm") version "1.7.21"
    id("io.ktor.plugin") version "2.3.0"
}

group = "somerfield"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("com.github.kittinunf.fuel:fuel:2.3.1")
    testImplementation("org.testcontainers:testcontainers:1.18.0")
    testImplementation("org.testcontainers:postgresql:1.18.0")

    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}