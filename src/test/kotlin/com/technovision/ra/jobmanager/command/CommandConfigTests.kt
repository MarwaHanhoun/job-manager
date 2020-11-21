package com.technovision.ra.jobmanager.command

import com.fasterxml.jackson.module.kotlin.readValue
import com.technovision.ra.jobmanager.utils.jsonPath.json
import com.technovision.ra.jobmanager.utils.jsonPath.jsonPath
import com.technovision.ra.jobmanager.utils.testObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*

@DisplayName("Command Config Specifications")
class CommandConfigTests {
    private val mapper = testObjectMapper
    @Nested
    @DisplayName("when serialize CommandConfig the result should")
    inner class SerializationTests {

        @Test
        fun `contains a field with name="type" with value="http" when HttpConfig is provided`() {
            val cfg = CommandConfig.HttpConfig(url = "http://localhost:8080")
            val parsed = mapper.writeValueAsString(cfg).json()

            assertAll("should serialize as http config", {
                assertThat(parsed.read<String>("$.type")).isEqualTo("http")
            }, {
                assertThat(parsed.read<String>("$.url")).isEqualTo(cfg.url)
            })

        }

        @Test
        fun `contains a field with name="type" with value="shell" when ShellConfig is provided`() {
            val cfg = CommandConfig.ShellConfig(script = "ls -l")
            val json = mapper.writeValueAsString(cfg).json()

            assertAll("should serialize as shell config", {
                assertThat(json.jsonPath<String>("$.type")).isEqualTo("shell")
            }, {
                assertThat(json.jsonPath<String>("$.script")).isEqualTo(cfg.script)
            })
        }
    }

    @Nested
    @DisplayName("when deserialize CommandConfig from json the result should")
    inner class DeserializationTests {

        @Test
        fun `be HttpConfig when the type is http`() {
            val json = """{
            |"type":"http", 
            |"url":"",
            |"headers":{},
            |"queryParams":{},
            |"method":"GET"}
            |""".trimMargin()

            val result = mapper.readValue<CommandConfig>(json)
            Assertions.assertTrue(result is CommandConfig.HttpConfig)
        }

        @Test
        fun `be ShellConfig when the type is shell`() {
            val json = """{
            |"type":"shell", 
            |"script":"ls -l",
            |"workingDirectory":"~"}
            |""".trimMargin()

            val result = mapper.readValue<CommandConfig>(json)
            Assertions.assertTrue(result is CommandConfig.ShellConfig)
        }

        @Test
        fun `contains items with right types when json array is provided`() {
            val json = """[{
            |"type":"shell", 
            |"script":"ls -l",
            |"workingDirectory":"~"},{
            |"type":"http", 
            |"url":"",
            |"headers":{},
            |"queryParams":{},
            |"method":"GET"}
            |]""".trimMargin()

            val result = mapper.readValue<List<CommandConfig>>(json)
            Assertions.assertTrue(result[0] is CommandConfig.ShellConfig)
            Assertions.assertTrue(result[1] is CommandConfig.HttpConfig)
        }
    }
}