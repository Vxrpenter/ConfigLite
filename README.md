# Updaer

<a href="https://github.com/Vxrpenter/ConfigLite/blob/master/LICENSE"><img src="https://img.shields.io/github/license/Vxrpenter/Updater?style=flat-square&logo=amazoniam&logoSize=amg&logoColor=forestgreen&label=Licenced%20Under&color=077533"/></a>&nbsp;
<a href="https://vxrpenter.github.io/ConfigLite/"><img src="https://img.shields.io/badge/KDoc-Online-forestgreen?style=flat-square&logo=kotlin&logoSize=amg"/></a>&nbsp;

A lightweight, easy to use, configuration wrapper

## Installation

<a href="https://central.sonatype.com/artifact/io.github.vxrpenter/config-lite"><img src="https://img.shields.io/maven-central/v/io.github.vxrpenter/config-lite?style=flat-square&logo=apachemaven&logoColor=f18800&color=f18800"></a>

### Gradle
```gradle
dependencies {
  implementation("io.github.vxrpenter:config-lite:VERSION")
}
```

### Maven
```xml
<dependency>
    <groupId>io.github.vxrpenter</groupId>
    <artifactId>config-lite</artifactId>
    <version>VERSION</version>
</dependency>
```
*Replace `VERSION` with the latest version*

## Getting Started
First create a configuration data class that houses the values and add an instance companion object:

```kotlin
@Serializable
data class Test(
    val option1: String,
    val option2: String,
) {
    companion object {
        val instance by lazy {
            ConfigLite.load<Test>("test.json")
        }
    }
}
```

Then register the config, e.g. in the main and then call the instance to access it:

```kotlin
fun main() {
    ConfigLite.register(System.getProperty("user.dir"), "/configs", "test.json")

    val config = Test.instance
}
```
