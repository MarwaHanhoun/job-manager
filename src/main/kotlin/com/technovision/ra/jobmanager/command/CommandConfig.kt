package com.technovision.ra.jobmanager.command

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import org.springframework.http.HttpMethod
import kotlin.reflect.full.findAnnotation


private const val HTTP_TYPE = "http"
private const val SHELL_TYPE = "shell"

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
sealed class CommandConfig(val type: String) {

    @JsonTypeName(HTTP_TYPE)
    class HttpConfig(val url: String,
                     val method: HttpMethod = HttpMethod.GET,
                     val queryParams: Map<String, String> = mapOf(),
                     val headers: Map<String, String> = mapOf(),
                     val body: String? = null
    ) : CommandConfig(HTTP_TYPE)

    @JsonTypeName(SHELL_TYPE)
    class ShellConfig(
            val script: String,
            val workingDirectory: String = "~",
            val host: String? = null,
            val port: Int? = null,
            val username: String? = null,
            val password: String? = null
    ) : CommandConfig(SHELL_TYPE)


    companion object {
        val types = CommandConfig::class.sealedSubclasses.map {
            it.findAnnotation<JsonTypeName>()!!.value
        }
    }
}