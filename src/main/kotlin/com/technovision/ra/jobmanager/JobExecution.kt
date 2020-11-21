package com.technovision.ra.jobmanager

import com.technovision.ra.jobmanager.command.CommandFactory
import com.technovision.ra.jobmanager.jpa.InstanceResult
import com.technovision.ra.jobmanager.jpa.JobConfigRepository
import com.technovision.ra.jobmanager.jpa.JobInstance
import com.technovision.ra.jobmanager.jpa.JobInstanceRepository
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.quartz.JobListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Component
class JobExecution : QuartzJobBean() {
    @Autowired
    lateinit var factory: CommandFactory

    override fun executeInternal(ctx: JobExecutionContext) {
        val cmd = factory.createCommandByConfigId(ctx.trigger.key.name)
        ctx.result = cmd.exec()
        if(!cmd.success)
            throw JobExecutionException()
    }
}


@Component
class LogJob(
        val instances: JobInstanceRepository,
        val configs: JobConfigRepository
) : JobListener {

    override fun getName(): String = "Logger"

    override fun jobToBeExecuted(ctx: JobExecutionContext) {
        val id = UUID.fromString(ctx.trigger.key.name)
        configs.findByIdOrNull(id)?.let { config ->
            ctx.mergedJobDataMap["instance_id"] =
                    instances.save(JobInstance(config = config, startedAt = LocalDateTime.now())).id.toString()
        }
    }

    override fun jobExecutionVetoed(p0: JobExecutionContext?) {

    }

    override fun jobWasExecuted(ctx: JobExecutionContext, ex: JobExecutionException?) {
        ctx.mergedJobDataMap.getString("instance_id")?.let { id ->
            instances.findById(UUID.fromString(id))
                    .ifPresent {
                        instances.save(it.copy(
                                finishedAt = ctx.trigger.endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                                result = ex?.let {
                                    InstanceResult.failure
                                } ?: InstanceResult.success,
                                output = ctx.result as String
                        ))
                    }
        }
    }

}

