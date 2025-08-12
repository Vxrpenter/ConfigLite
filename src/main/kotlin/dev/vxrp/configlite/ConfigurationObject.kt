package dev.vxrp.configlite

import java.nio.file.Path

data class ConfigurationObject(
    val location: Path,
    val fileName: String,
    val type: ConfigType?
)
