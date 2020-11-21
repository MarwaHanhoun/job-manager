package com.technovision.ra.jobmanager.command

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.net.URI


@DisplayName("Http Command Specifications")
class HttpCommandTests {

    private fun mockResponseEntity(responseProvider: () -> ResponseEntity<String>): RestTemplate =
            Mockito.mock(RestTemplate::class.java).apply {
                Mockito.`when`(execute<ResponseEntity<String>>(
                        Mockito.any<URI>(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any()
                )).thenReturn(responseProvider())
            }

    @Nested
    @DisplayName("when executed it should")
    inner class TestCommandExecution {
        private val config = CommandConfig.HttpConfig(url = "http://localhost:8080")

        private val successResponse = mockResponseEntity { ResponseEntity.ok("Hello") }
        private val notFoundResponse = mockResponseEntity { ResponseEntity.notFound().build() }
        private val internalServerError = mockResponseEntity {
            ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error")
        }

        fun RestTemplate.command() =
                HttpCommand(this, config)

        @Test
        fun `return response body as string when there is a body`() {
            successResponse.command().apply {
                assertThat(exec()).isEqualTo("Hello")
            }

            internalServerError.command().apply {
                assertThat(exec()).isEqualTo("Internal Server Error")
            }
        }

        @Test
        fun `return empty string when there is no response body`() {
            notFoundResponse.command().apply {
                assertThat(exec()).isEmpty()
            }
        }

        @Test
        fun `success when response status code is not error`() {
            HttpCommand(successResponse, config).apply {
                exec()
                assertTrue(success)
            }
        }

        @Test
        fun `not success when response status code is error`() {
            HttpCommand(notFoundResponse, config).apply {
                exec()
                assertFalse(success)
            }
        }

    }

    @Nested
    @DisplayName("when buildUrl the result should")
    inner class BuildUriTests {
        private val queryParams = mapOf(
                "param1" to "value1",
                "param2" to "value 2"
        )
        private val uri = HttpCommand(
                mockResponseEntity { ResponseEntity.ok("") },
                CommandConfig.HttpConfig(
                        "http://localhost:8080/api",
                        queryParams = queryParams
                )
        ).buildUri()

        @Test
        fun `contains right host and port`() {
            assertThat(uri.host).isEqualTo("localhost")
            assertThat(uri.port).isEqualTo(8080)
        }

        @Test
        fun `contains all query params`() {
            assertThat(uri.query)
                    .contains(queryParams.map { "${it.key}=${it.value}" })
        }

        @Test
        fun `contains the right path`() {
            assertThat(uri.path).isEqualTo("/api")
        }

        @Test
        fun `contains the right protocol`() {
            assertThat(uri.toURL().protocol).isEqualTo("http")
        }
    }

}