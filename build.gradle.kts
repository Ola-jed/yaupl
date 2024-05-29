plugins {
    kotlin("jvm") version "1.9.23"
    application
}

group = "com.ola"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.getByName<JavaExec>("run") {
    standardInput = System.`in`
}

application {
    mainClass = "MainKt"
}

kotlin {
    jvmToolchain(17)
}