package com.technovision.ra.jobmanager

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.technovision.ra.jobmanager.command.CommandConfig
import com.technovision.ra.jobmanager.jpa.JobConfigRepository
import com.technovision.ra.jobmanager.jpa.JobConfiguration
import com.technovision.ra.jobmanager.jpa.JobConfigurationStatus
import com.technovision.ra.jobmanager.jpa.JobInstanceRepository
import org.quartz.JobBuilder.newJob
import org.quartz.JobDetail
import org.quartz.Scheduler
import org.quartz.SimpleScheduleBuilder
import org.quartz.TriggerBuilder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@Component
class JobService(
        val jobConfigRepository: JobConfigRepository,
        val jobInstanceRepository: JobInstanceRepository,
        val scheduler: Scheduler,
        val json: ObjectMapper
) {

    private fun createQuartzTrigger(id: UUID, interval: Int, start: Boolean = false) =
            TriggerBuilder.newTrigger()
                    .withIdentity(id.toString())
                    .withSchedule(
                            SimpleScheduleBuilder
                                    .simpleSchedule()
                                    .withIntervalInMinutes(interval)
                                    .repeatForever()
                    ).apply {
                        if (start)
                            startNow()
                    }.build()


    private fun createQuartzJob(
            id: UUID
    ): JobDetail = newJob().ofType(JobExecution::class.java)
            .withIdentity(id.toString()).storeDurably()
            .build()


    private fun JobConfiguration.asDto() =
            JobDto(
                    id = id,
                    name = name,
                    interval = interval,
                    status = status,
                    config = json.readValue(config)
            )


    @Transactional
    fun addJobConfig(job: JobDto) =
            JobConfiguration(
                    name = job.name,
                    interval = job.interval,
                    status = job.status,
                    type = job.config.type,
                    config = json.writeValueAsString(job.config)
            ).let {
                jobConfigRepository.save(it)
            }.let {
                scheduler.scheduleJob(
                        createQuartzJob(it.id),
                        createQuartzTrigger(it.id, it.interval)
                )
                job.copy(id = it.id)
            }


    fun listJobConfigs(
            typeIn: List<String>,
            statusIn: List<JobConfigurationStatus>,
            nameLike: String
    ) = jobConfigRepository
            .findAllByTypeInAndStatusInAndNameIsLike(
                    typeIn,
                    statusIn,
                    nameLike
            ).map { it.asDto() }


    fun findOne(id: UUID): JobDto? =
            jobConfigRepository.findById(id).map { it.asDto() }.orElse(null)

    fun findInstances(id: UUID, after: LocalDateTime, before: LocalDateTime) =
            jobInstanceRepository.findAllByConfig_IdAndStartedAtBetween(id, after, before)
}


data class JobDto(
        val id: UUID? = null,
        val name: String,
        val interval: Int,
        val status: JobConfigurationStatus = JobConfigurationStatus.started,
        val config: CommandConfig,
)




