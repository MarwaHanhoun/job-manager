package com.technovision.ra.jobmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JobManagerApplication

fun main(args: Array<String>) {
    runApplication<JobManagerApplication>(*args)
}
