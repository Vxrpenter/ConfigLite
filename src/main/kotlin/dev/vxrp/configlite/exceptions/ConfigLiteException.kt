package dev.vxrp.configlite.exceptions

import java.lang.Exception

open class ConfigLiteException(message: String, cause: Throwable? = null) : Exception(message, cause)