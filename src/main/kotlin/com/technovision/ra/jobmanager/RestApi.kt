package com.technovision.ra.jobmanager

import com.technovision.ra.jobmanager.command.CommandConfig
import com.technovision.ra.jobmanager.jpa.JobConfigurationStatus
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("api/jobs")

class JobController(val jobService: JobService) {

    @PostMapping
    fun addJob(@RequestBody details: JobDto) =
            jobService.addJobConfig(details)

    @GetMapping
    fun list(@RequestParam
             type: List<String> = CommandConfig.types,
             @RequestParam
             status: List<JobConfigurationStatus> = JobConfigurationStatus.values().toList(),
             @RequestParam
             nameLike: String = "*"
    ) = jobService.listJobConfigs(type, status, nameLike)

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: UUID) = jobService.findOne(id)?.let {
        ok(it)
    } ?: notFound().build()

    @GetMapping("/{id}/instances")
    fun getInstances(
            @PathVariable id: UUID,
            @RequestParam before: LocalDateTime = LocalDateTime.now(),
            @RequestParam after: LocalDateTime = LocalDateTime.now().minusDays(5)
    ) = jobService.findInstances(
            id, after, before
    )
}