package dev.vxrp.configlite

open class ConfigLite {
    companion object Default : ConfigLite()

    fun register(name: String, type: ConfigType) {

    }

    inline  fun <reified T> load(name: String): T {

    }
}