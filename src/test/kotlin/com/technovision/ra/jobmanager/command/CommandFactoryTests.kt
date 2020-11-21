package com.technovision.ra.jobmanager.command

import com.fasterxml.jackson.module.kotlin.readValue
import com.technovision.ra.jobmanager.utils.testObjectMapper
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.web.client.RestTemplateBuilder
import java.util.*


@DisplayName("Command Factory Specifications")
class CommandFactoryTests {
    val configMap = listOf(
            UUID.randomUUID() to """{"type":"http","url":"http://localhost:8080"}""",
            UUID.randomUUID() to """{"type":"shell","script":"ls -l"}""",
            UUID.randomUUID() to """{"type":"hello"}"""
    )

    val commandFactory = CommandFactory(
            getConfigs = { id -> configMap.firstOrNull { it.first == id }?.second },
            fromJson = { testObjectMapper.readValue(it) },
            restClient = RestTemplateBuilder().build()
    )

    @Nested
    @DisplayName("when createCommandByConfig it should create")
    inner class TestCreateCommandByConfig {

        val testTarget = commandFactory::createCommandByConfig

        @Test
        fun `ShellCommand when ShellConfig is provided`() {
            val shellConfig = CommandConfig.ShellConfig(script = "ls -l")
            assertTrue(testTarget(shellConfig) is ShellCommand)
        }

        @Test
        fun `HttpCommand when HttpConfig is provided`() {
            val httpConfig = CommandConfig.HttpConfig(url = "http://localhost:8080")
            assertTrue(testTarget(httpConfig) is HttpCommand)
        }
    }


    @Nested
    @DisplayName("when createCommandByConfigId it should create")
    inner class TestCreateCommandByConfigId {
        val testTarget = commandFactory::createCommandByConfigId

        @Test
        fun `NOOPCommand when config not found`() {
            assertTrue(testTarget(UUID.randomUUID().toString()) is NOOP)
        }

        @Test
        fun `NOOPCommand when id is not valid UUID`() {
            assertTrue(testTarget("Hello World") is NOOP)
        }

        @Test
        fun `NOOPCommand when config contains not valid json`() {
            assertTrue(testTarget(configMap[2].second.toString()) is NOOP)
        }

        @Test
        fun `ShellCommand when ShellConfig Id is provided`() {
            assertTrue(testTarget(configMap[1].first.toString()) is ShellCommand)
        }

        @Test
        fun `HttpCommand when HttpConfig Id is provided`() {
            assertTrue(testTarget(configMap[0].first.toString()) is HttpCommand)
        }

    }

}