package com.technovision.ra.jobmanager.command

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RequestCallback
import org.springframework.web.client.ResponseExtractor
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI


class HttpCommand(val template: RestTemplate, val config: CommandConfig.HttpConfig) : Command {
    override var success: Boolean = false

    private val requestCallback = RequestCallback { req ->
        req.headers.addAll()
        config.body?.let { body ->
            req.body.writer().use { it.write(body) }
        }
    }

    private val responseExtractor = ResponseExtractor<ResponseEntity<String>> {
        ResponseEntity(it.body.reader().use { reader -> reader.readText() }, it.statusCode)
    }

    override fun exec(): String {
        return template.execute(buildUri(), config.method, requestCallback, responseExtractor
        )?.let {
            success = !it.statusCode.isError
            it.body
        } ?: ""
    }


    fun buildUri(): URI =
            UriComponentsBuilder.fromHttpUrl(config.url).apply {
                config.queryParams.forEach { (k, v) ->
                    queryParam(k, v)
                }
            }.build().toUri()


    private fun HttpHeaders.addAll() {
        config.headers.forEach { (k, v) ->
            add(k, v)
        }
    }
}

