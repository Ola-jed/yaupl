group = "com.ola"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "2.2.0"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.14"
    kotlin("plugin.allopen") version "2.0.20"
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lang"))
    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.13")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

benchmark {
    configurations {
        named("main") {
            warmups = 5
            iterations = 5
            iterationTime = 1
            iterationTimeUnit = "s"
        }
    }

    targets {
        register("main")
    }
}

tasks.register("prepareKotlinBuildScriptModel"){}
