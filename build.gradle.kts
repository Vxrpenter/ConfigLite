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
}



kotlin {
    jvmToolchain(24)
}