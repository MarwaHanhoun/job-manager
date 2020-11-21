package com.technovision.ra.jobmanager

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.technovision.ra.jobmanager.command.CommandFactory
import com.technovision.ra.jobmanager.jpa.JobConfigRepository
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.client.RestTemplate

@Configuration
class JobManagerConfiguration {

    @Bean
    fun restTemplate(): RestTemplate =
            RestTemplateBuilder().build()

    @Bean
    fun commandFactory(jobConfigRepository: JobConfigRepository,
                       objectMapper: ObjectMapper,
                       restTemplate: RestTemplate): CommandFactory = CommandFactory(
            { jobConfigRepository.findByIdOrNull(it)?.config },
            { objectMapper.readValue(it) },
            restTemplate
    )

    @Bean
    fun schedulerFactoryBeanCustomizer(log: LogJob) = SchedulerFactoryBeanCustomizer {
                it.setGlobalJobListeners(log)
    }
}