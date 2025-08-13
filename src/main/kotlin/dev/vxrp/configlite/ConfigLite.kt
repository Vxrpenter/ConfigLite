package dev.vxrp.configlite

import com.charleskorn.kaml.Yaml
import dev.vxrp.configlite.exceptions.ResourceNotFound
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.peanuuutz.tomlkt.Toml
import net.peanuuutz.tomlkt.decodeFromString
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists

val registeredConfigurationObjects = hashMapOf<String, ConfigurationObject>()

open class ConfigLite {
    companion object Default : ConfigLite()

    @JvmOverloads
    fun register(headDirectory: String, location: String, name: String, type: ConfigType? = null) {
        val configDirectory = Path("$headDirectory$location")

        registeredConfigurationObjects[name] = ConfigurationObject(
            location = configDirectory,
            fileName = name,
            type = type
        )

        val file = Path("$configDirectory/$name").toFile()

        if (configDirectory.notExists()) configDirectory.createDirectories()
        if (!file.exists()) {
            var contentDirectory = name
            location.isBlank().let { contentDirectory = "$location/$name" }
            val content = ConfigLite::class.java.getResourceAsStream(contentDirectory)
            file.createNewFile()

            if (content == null) throw ResourceNotFound("Could not find resource $contentDirectory")
            file.appendBytes(content.readAllBytes())
        }
    }

    inline fun <reified T> load(name: String): T? {
        val configuration = registeredConfigurationObjects[name] ?: return null

        return serializedConfiguration<T>(configuration)
    }

    inline fun <reified T> serializedConfiguration(configurationObject: ConfigurationObject): T? {
        println("${configurationObject.location}/${configurationObject.fileName}")
        val file = Path("${configurationObject.location}/${configurationObject.fileName}").toFile()
        val fileContents = file.readText()

        if (configurationObject.type == null) {
            if (configurationObject.fileName.endsWith(".json")) return decodeJson(fileContents)
            if (configurationObject.fileName.endsWith(".yml")) return decodeYaml(fileContents)
            if (configurationObject.fileName.endsWith(".toml")) return decodeToml(fileContents)
        }

        return when(configurationObject.type) {
            ConfigType.JSON -> decodeJson(fileContents)
            ConfigType.YAML -> decodeYaml(fileContents)
            ConfigType.TOML -> decodeToml(fileContents)
            null -> null
        }
    }

    inline fun <reified T> decodeJson(fileContents: String) = Json.decodeFromString<T>(fileContents)
    inline fun <reified T> decodeYaml(fileContents: String) = Yaml.default.decodeFromString<T>(fileContents)
    inline fun <reified T> decodeToml(fileContents: String) =  Toml.decodeFromString<T>(fileContents)
}