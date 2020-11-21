package com.technovision.ra.jobmanager.jpa

import com.technovision.ra.jobmanager.command.CommandConfig
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "TECHNOVISION_JOBS")
class JobConfiguration(
        @Id
        @GeneratedValue
        val id: UUID = UUID.randomUUID(),
        val name: String = "",
        @Column(name = "jon_interval")
        val interval: Int = 10,
        @Column(name = "job_type")
        val type: String = "",
        val status: JobConfigurationStatus = JobConfigurationStatus.started,
        val config: String = "{}"
)

enum class JobConfigurationStatus {
    started, stopped
}


interface JobConfigRepository : JpaRepository<JobConfiguration, UUID> {
    fun findAllByTypeInAndStatusInAndNameIsLike(
            type: List<String> = CommandConfig.types,
            status: List<JobConfigurationStatus> = JobConfigurationStatus.values().toList(),
            nameLike: String = "%"
    ): List<JobConfiguration>
}
