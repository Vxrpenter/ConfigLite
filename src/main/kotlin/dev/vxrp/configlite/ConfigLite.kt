package dev.vxrp.configlite

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import dev.vxrp.configlite.exceptions.MalformedConfigurationObjectException
import dev.vxrp.configlite.exceptions.RegisteredObjectNotFoundException
import dev.vxrp.configlite.exceptions.ResourceNotFoundException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.peanuuutz.tomlkt.Toml
import net.peanuuutz.tomlkt.decodeFromString
import java.io.FileNotFoundException
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists

val registeredConfigurationObjects = hashMapOf<String, ConfigurationObject>()

open class ConfigLite {
    companion object Default : ConfigLite()

    /**
     * Registering a configuration marks a configuration file as active.
     * It then gets created and populated, with a key being stored for access to the configuration file.
     *
     * @param headDirectory The head directory on the targeted system
     * @param location Where should the configuration be created (excluded [headDirectory])
     * @param name The filename of the configuration
     * @param type The type of the configuration (optional)
     * @throws kotlinx.serialization.SerializationException
     */
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
                ?: throw ResourceNotFoundException("Could not find resource $contentDirectory")

            file.createNewFile()
            file.appendBytes(content.readAllBytes())
        }
    }

    /**
     * Registering a configuration marks a configuration file as active.
     * It then gets created and populated with a specified [config] value instead of a resource file,
     * with a key being stored for access to the configuration file.
     *
     * @param headDirectory The head directory on the targeted system
     * @param location Where should the configuration be created (excluded [headDirectory])
     * @param name The filename of the configuration
     * @param config The configuration to write
     * @param type The type of the configuration (optional)
     * @throws kotlinx.serialization.SerializationException
     */
    inline fun <reified T> registerDirect(headDirectory: String, location: String, name: String, config: T, type: ConfigType? = null) {
        val configDirectory = Path("$headDirectory$location")

        val configurationObject = ConfigurationObject(
            location = configDirectory,
            fileName = name,
            type = type
        )
        registeredConfigurationObjects[name] = configurationObject

        val file = Path("$configDirectory/$name").toFile()

        if (configDirectory.notExists()) configDirectory.createDirectories()
        if (!file.exists()) {
            val content = encodeConfiguration<T>(configurationObject, config)

            file.createNewFile()
            file.appendText(content)
        }
    }

    /**
     * Loads the configuration using the entered configuration class [T] by retrieving its file from the
     * [register]'ed configurations.
     *
     * @param T The configuration's serialization class
     * @param name The filename of the configuration
     * @return The serialized configuration object
     * @throws kotlinx.serialization.SerializationException
     * @throws RegisteredObjectNotFoundException
     */
    inline fun <reified T> load(name: String): T {
        val configuration = registeredConfigurationObjects[name] ?: throw RegisteredObjectNotFoundException("Could not find configuration file $name in registered configuration objects $registeredConfigurationObjects")

        return decodeConfiguration<T>(configuration)
    }

    inline fun <reified T> encodeConfiguration(configurationObject: ConfigurationObject, config: T): String {
        if (configurationObject.type == null) {
            if (configurationObject.fileName.endsWith(".json")) return encodeJson(config)
            if (configurationObject.fileName.endsWith(".yml")) return encodeYaml(config)
            if (configurationObject.fileName.endsWith(".toml")) return encodeToml(config)
        }

        return when(configurationObject.type) {
            ConfigType.JSON -> encodeJson(config)
            ConfigType.YAML -> encodeYaml(config)
            ConfigType.TOML -> encodeToml(config)
            null -> throw MalformedConfigurationObjectException("Configuration object $configurationObject filetype could not be determined for encoding")
        }
    }

    inline fun <reified T> decodeConfiguration(configurationObject: ConfigurationObject): T {
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
            null -> throw FileNotFoundException("Could not find configuration at $file")
        }
    }

    val json = Json { prettyPrint = true }
    val yaml = Yaml
    val toml = Toml

    inline fun <reified T> decodeJson(fileContents: String) = json.decodeFromString<T>(fileContents)
    inline fun <reified T> decodeYaml(fileContents: String) = yaml.default.decodeFromString<T>(fileContents)
    inline fun <reified T> decodeToml(fileContents: String) =  toml.decodeFromString<T>(fileContents)

    inline fun <reified T> encodeJson(config: T) = json.encodeToString<T>(config)
    inline fun <reified T> encodeYaml(config: T) = yaml.default.encodeToString<T>(config)
    inline fun <reified T> encodeToml(config: T) =  toml.encodeToString<T>(config)
}