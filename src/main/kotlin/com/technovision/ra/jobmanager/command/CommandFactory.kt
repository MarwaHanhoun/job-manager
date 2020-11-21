package com.technovision.ra.jobmanager.command

import org.springframework.web.client.RestTemplate
import java.util.*


class CommandFactory(
        private val getConfigs: (id: UUID) -> String?,
        private val fromJson: (String) -> CommandConfig,
        private val restClient: RestTemplate
) {

    fun createCommandByConfigId(id: String): Command =
            runCatching {
                getConfigs(UUID.fromString(id))
                        ?.let { cfg -> fromJson(cfg) }
                        ?.let { cfg -> createCommandByConfig(cfg) }
            }.getOrNull() ?: NOOP

    fun createCommandByConfig(config: CommandConfig): Command =
            when (config) {
                is CommandConfig.HttpConfig -> HttpCommand(restClient, config)
                is CommandConfig.ShellConfig -> ShellCommand(config)
            }
}



