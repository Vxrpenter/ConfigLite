plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
}

group = "io.github.vxrpenter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    api("org.jetbrains.kotlinx:kotlinx-serialization-properties:1.9.0")
    api("net.peanuuutz.tomlkt:tomlkt:0.5.0")
    api("com.charleskorn.kaml:kaml:0.85.0")
}



kotlin {
    jvmToolchain(24)
}