plugins {
    kotlin("jvm") version "1.9.23"
}

group = "com.ola"
version = "unspecified"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}