package com.technovision.ra.jobmanager.jpa

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class JobConfigurationTests {
    @Autowired
    lateinit var repo: JobConfigRepository

    @BeforeEach
    fun setup() {
        repo.save(JobConfiguration(
                name = "job 1",
                status = JobConfigurationStatus.started,
                type = "http",
                config = """{"type":"http","url":"http://localhost:8080"}"""
        ))
    }

    @Nested
    inner class FetchByType {
        @Test
        fun `should return jobs with matching type`() {
            val type = "http"
            assertThat(repo.findAllByTypeInAndStatusInAndNameIsLike(type = listOf(type)))
                    .allMatch { it.type == type }
                    .hasSize(1)
        }
    }


}