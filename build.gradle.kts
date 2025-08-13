import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URI

buildscript {
    dependencies {
        classpath("org.jetbrains.dokka:dokka-base:2.0.0")
    }
}

plugins {
    id("org.jetbrains.dokka") version "2.0.0"
    id("com.vanniktech.maven.publish") version "0.32.0"
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
}

group = "io.github.vxrpenter"
version = "0.1.1"

repositories {
    mavenCentral()
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    api("org.jetbrains.kotlinx:kotlinx-serialization-properties:1.9.0")
    api("net.peanuuutz.tomlkt:tomlkt:0.5.0")
    api("com.charleskorn.kaml:kaml:0.85.0")
}

tasks.getByName("dokkaHtml", DokkaTask::class) {
    dokkaSourceSets.configureEach {
        includes.from("packages.md")
        jdkVersion.set(8)
        sourceLink {
            localDirectory.set(file("src/master/kotlin"))
            remoteUrl.set(URI("https://github.com/Vxrpenter/ConfigLite/tree/master/src/main/kotlin").toURL())
            remoteLineSuffix.set("#V")
        }

        pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
            footerMessage = "Copyright © 2025 Vxrpenter"
        }
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(group.toString(), "config-lite")

    pom {
        name = "ConfigLite"
        description = "Lightweight configuration wrapper"
        inceptionYear = "2025"
        url = "https://github.com/Vxrpenter/ConfigLite"
        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/license/mit"
            }
        }
        developers {
            developer {
                name = "Vxrpenter"
                url = "https://github.com/Vxrpenter"
            }
        }
        scm {
            url = "https://github.com/Vxrpenter/ConfigLite"
            connection = "scm:git:git://github.com/Vxrpenter/ConfigLite.git"
            developerConnection = "scm:git:ssh://git@github.com/Vxrpenter/ConfigLite.git"
        }
    }
}