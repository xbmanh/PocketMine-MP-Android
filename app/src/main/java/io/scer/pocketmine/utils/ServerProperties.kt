package io.scer.pocketmine.utils

import io.scer.pocketmine.server.Server
import java.lang.IndexOutOfBoundsException
import java.lang.String.valueOf
import java.text.SimpleDateFormat
import java.util.*

class ServerProperties {
    private val config = LinkedHashMap<String, Any>()

    init {
        read()
    }

    fun getMap(): LinkedHashMap<String, Any> {
        return config
    }

    fun get(key: String): Any? {
        return config[key]
    }

    fun set(key: String, value: Any) {
        config[key] = value
    }

    private fun read() {
        val content = Server.getInstance().files.serverSetting.inputStream().readBytes().toString(Charsets.UTF_8)
        for (line in content.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            val property = line.split("=")
            try {
                val key = property[0]
                val value = property[1].trim()
                val lowerCaseValue = value.toLowerCase()
                when (lowerCaseValue) {
                    "on", "true", "yes" -> this.config[key] = true
                    "off", "false", "no" -> this.config[key] = false
                    else -> this.config[key] = value
                }
            } catch (e: IndexOutOfBoundsException) {
                continue
            }
        }
    }

    fun write() {
        var content = "#Properties Config file\r\n#" + SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(Date()) + "\r\n"
        for (entry in config) {
            val key = entry.key
            var value: Any = entry.value
            if (value is Boolean) {
                when (value) {
                    true -> value = "on"
                    false -> value = "off"
                }
            }
            content += valueOf(key) + "=" + valueOf(value) + "\r\n"
        }
        Server.getInstance().files.serverSetting.writeText(content)
    }
}