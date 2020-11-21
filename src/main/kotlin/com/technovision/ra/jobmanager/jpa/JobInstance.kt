package com.technovision.ra.jobmanager.jpa

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "TECHNOVISION_JOB_INSTANCES")
data class JobInstance(
        @Id
        @GeneratedValue
        val id: UUID = UUID.randomUUID(),
        @field:ManyToOne
        val config: JobConfiguration = JobConfiguration(),
        val startedAt: LocalDateTime? = null,
        val finishedAt: LocalDateTime? = null,
        val result: InstanceResult = InstanceResult.pending,
        val output: String = ""
)

enum class InstanceResult {
    success, failure, pending
}

interface JobInstanceRepository : JpaRepository<JobInstance, UUID> {
    fun findAllByConfig_TypeAndStartedAtBetween(type: String, after: LocalDateTime, before: LocalDateTime):
            List<JobInstance>

    fun findAllByConfig_IdAndStartedAtBetween(id: UUID, after: LocalDateTime, before: LocalDateTime):List<JobInstance>
}

