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
    implementation(project(":lang"))
    implementation("net.bytebuddy:byte-buddy:1.15.1")
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